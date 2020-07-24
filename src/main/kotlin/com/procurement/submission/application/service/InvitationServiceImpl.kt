package com.procurement.submission.application.service

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.repository.InvitationRepository
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.ValidationResult
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.functional.asValidationFailure
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import org.springframework.stereotype.Service

@Service
class InvitationServiceImpl(
    private val invitationRepository: InvitationRepository,
    private val generationService: GenerationService
) : InvitationService {

    override fun doInvitations(params: DoInvitationsParams): Result<DoInvitationsResult?, Fail> {
        checkForMissingSubmissions(params).doOnFail { error -> return error.asFailure() }

        val canceledAndNewInvitations = getCanceledAndNewInvitations(params)
            .orForwardFail { fail -> return fail }

        if (canceledAndNewInvitations.isEmpty())
            return null.asSuccess()

        invitationRepository.saveAll(cpid = params.cpid, invitations = canceledAndNewInvitations)

        return DoInvitationsResult(
            invitations = canceledAndNewInvitations.map { invitation ->
                DoInvitationsResult.Invitation(
                    id = invitation.id,
                    date = invitation.date,
                    status = invitation.status,
                    relatedQualification = invitation.relatedQualification,
                    tenderers = invitation.tenderers.map { tenderer ->
                        DoInvitationsResult.Invitation.Tenderer(
                            id = tenderer.id,
                            name = tenderer.name
                        )
                    }
                )
            }
        ).asSuccess()
    }

    fun getCanceledAndNewInvitations(params: DoInvitationsParams): Result<List<Invitation>, Fail> {
        val invitations = invitationRepository.findBy(cpid = params.cpid)
            .orForwardFail { fail -> return fail }

       return mutableListOf<Invitation>()
            .apply {
                addCancelledInvitations(this, invitations, params)
                addNewInvitations(this, invitations, params)
            }.toList()
            .asSuccess()
    }

    private fun addNewInvitations(
        cancelledAndNewInvitations: MutableList<Invitation>,
        storedInvitations: List<Invitation>,
        params: DoInvitationsParams
    ) {
        val invitationsByQualification = storedInvitations.groupBy { it.relatedQualification }
        val submissionsByIds = params.submissions.details.associateBy { it.id }

        params.qualifications.forEach { qualification ->
            val linkedInvitations = invitationsByQualification[qualification.id] ?: emptyList()

            if (pendingInvitationAbsent(linkedInvitations)) {
                val createdInvitation = createInvitation(params, qualification, submissionsByIds)
                cancelledAndNewInvitations.add(createdInvitation)
            }
        }
    }

    private fun addCancelledInvitations(
        cancelledAndNewInvitations: MutableList<Invitation>,
        storedInvitations: List<Invitation>,
        params: DoInvitationsParams
    ) {
        val receivedQualifications = params.qualifications.toSetBy { it.id }

        val pendingInvitations = storedInvitations.filter { it.status == InvitationStatus.PENDING }
        val invitationsNotLinkedToQualification = pendingInvitations.filter { it.relatedQualification !in receivedQualifications }
        val canceledInvitations = invitationsNotLinkedToQualification.map { it.copy(status = InvitationStatus.CANCELLED) }

        cancelledAndNewInvitations.addAll(canceledInvitations)
    }

    override fun checkAbsenceActiveInvitations(params: CheckAbsenceActiveInvitationsParams): ValidationResult<Fail> {

        val activeInvitationsFromDb = invitationRepository.findBy(cpid = params.cpid)
            .doReturn { error -> return error.asValidationFailure() }
            .filter { it.status == InvitationStatus.PENDING }

        return if (activeInvitationsFromDb.isNotEmpty())
            ValidationResult.error(ValidationError.ActiveInvitationsFound(activeInvitationsFromDb.map { it.id }))
        else
            ValidationResult.ok()
    }

    private fun pendingInvitationAbsent(invitations: List<Invitation>) = invitations.none { it.status == InvitationStatus.PENDING }

    private fun createInvitation(
        params: DoInvitationsParams,
        qualification: DoInvitationsParams.Qualification,
        submissionsByIds: Map<SubmissionId, DoInvitationsParams.Submissions.Detail>
    ) = Invitation(
        id = generationService.generateInvitationId(),
        status = InvitationStatus.PENDING,
        date = params.date,
        relatedQualification = qualification.id,
        tenderers = submissionsByIds.getValue(qualification.relatedSubmission)
            .candidates.map { candidate ->
                Invitation.Tenderer(
                    id = candidate.id, name = candidate.name
                )
            }
    )

    private fun getInvitationsBy(cpid: Cpid, qualificationId: QualificationId): Result<List<Invitation>, Fail> {
        val invitations = invitationRepository.findBy(cpid = cpid)
            .orForwardFail { fail -> return fail }

        return invitations.filter { invitation ->
            invitation.relatedQualification == qualificationId
        }.asSuccess()
    }

    private fun checkForMissingSubmissions(params: DoInvitationsParams): MaybeFail<ValidationError.MissingSubmission> {
        val relatedSubmissionsIds = params.qualifications.toSetBy { it.relatedSubmission }
        val submissionsIds = params.submissions.details.toSetBy { it.id }

        val missingSubmissions = relatedSubmissionsIds - submissionsIds

        if (missingSubmissions.isNotEmpty())
            return MaybeFail.fail(ValidationError.MissingSubmission(submissionIds = missingSubmissions))

        return MaybeFail.none()
    }
}