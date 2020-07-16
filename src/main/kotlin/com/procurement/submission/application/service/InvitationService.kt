package com.procurement.submission.application.service

import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.repository.InvitationRepository
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import org.springframework.stereotype.Service

@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val generationService: GenerationService
) {

    fun doInvitations(params: DoInvitationsParams): Result<DoInvitationsResult?, Fail> {
        checkForMissingSubmissions(params).doOnFail { error -> return error.asFailure() }

        val updatedAndNewInvitations = getUpdatedAndNewInvitations(params)
            .orForwardFail { fail -> return fail }

        if (updatedAndNewInvitations.isEmpty())
            return null.asSuccess()

        invitationRepository.saveAll(cpid = params.cpid, invitations = updatedAndNewInvitations)

        return DoInvitationsResult(
            invitations = updatedAndNewInvitations.map { invitation ->
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

    fun getUpdatedAndNewInvitations(params: DoInvitationsParams): Result<List<Invitation>, Fail> {
        val submissionsByIds = params.submissions.details.associateBy { it.id }
        val updatedAndNewInvitations = mutableListOf<Invitation>()

        params.qualifications.forEach { qualification ->
            val linkedInvitations = getInvitationsBy(cpid = params.cpid, qualificationId = qualification.id)
                .orForwardFail { fail -> return fail }

            when (qualification.statusDetails) {
                QualificationStatusDetails.ACTIVE -> {
                    if (pendingInvitationAbsent(linkedInvitations)) {
                        val createdInvitation = createInvitation(params, qualification, submissionsByIds)
                        updatedAndNewInvitations.add(createdInvitation)
                    }
                }
                QualificationStatusDetails.UNSUCCESSFUL -> {
                    val updatedInvitations = updateInvitations(linkedInvitations)
                    updatedAndNewInvitations.addAll(updatedInvitations)
                }
                QualificationStatusDetails.CONSIDERATION,
                QualificationStatusDetails.AWAITING -> Unit
            }
        }
        return updatedAndNewInvitations.toList().asSuccess()
    }

    private fun updateInvitations(
        linkedInvitations: List<Invitation>
    ): List<Invitation> {
        val pendingInvitations = linkedInvitations.filter { it.status == InvitationStatus.PENDING }
        return if (pendingInvitations.isNotEmpty()) {
            val updatedInvitations = pendingInvitations.map { invitation ->
                invitation.copy(status = InvitationStatus.CANCELLED)
            }
            updatedInvitations
        } else emptyList()
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