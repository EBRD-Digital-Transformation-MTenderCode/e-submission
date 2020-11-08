package com.procurement.submission.application.service

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import com.procurement.submission.infrastructure.dto.invitation.publish.PublishInvitationsResult
import com.procurement.submission.infrastructure.dto.invitation.publish.convert
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Result.Companion.failure
import com.procurement.submission.lib.functional.Result.Companion.success
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import org.springframework.stereotype.Service

@Service
class InvitationServiceImpl(
    private val invitationRepository: InvitationRepository,
    private val generationService: GenerationService,
    private val rulesService: RulesService
) : InvitationService {

    override fun doInvitations(params: DoInvitationsParams): Result<DoInvitationsResult?, Fail> {
        checkForMissingSubmissions(params).doOnFail { error -> return error.asFailure() }

        val invitationsToSave = getInvitationsToSave(params)
            .onFailure { return it }

        if (invitationsToSave.isEmpty())
            return null.asSuccess()

        val invitationsResponseIsNeeded = rulesService
            .getReturnInvitationsFlag(params.country, params.pmd, params.operationType)
            .onFailure { return it }

        invitationRepository.saveAll(cpid = params.cpid, invitations = invitationsToSave)

        if (!invitationsResponseIsNeeded)
            return null.asSuccess()

        return DoInvitationsResult(
            invitations = invitationsToSave.map { invitation ->
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

    private fun checkForMissingSubmissions(params: DoInvitationsParams): MaybeFail<ValidationError.MissingSubmission> {
        val relatedSubmissionsIds = params.qualifications.toSetBy { it.relatedSubmission }
        val submissionsIds = params.submissions?.details?.toSetBy { it.id } ?: emptySet()

        val missingSubmissions = relatedSubmissionsIds - submissionsIds

        if (missingSubmissions.isNotEmpty())
            return MaybeFail.fail(ValidationError.MissingSubmission(submissionIds = missingSubmissions))

        return MaybeFail.none()
    }

    fun getInvitationsToSave(params: DoInvitationsParams): Result<List<Invitation>, Fail> {
        val invitations = invitationRepository.findBy(cpid = params.cpid)
            .onFailure { return it }

        return mutableListOf<Invitation>()
            .apply {
                addAll(getUpdatedInvitations(invitations, params))
                addAll(getNewInvitations(invitations, params))
            }
            .toList()
            .asSuccess()
    }

    private fun getUpdatedInvitations(
        storedInvitations: List<Invitation>,
        params: DoInvitationsParams
    ): List<Invitation> {
        val receivedQualifications = params.qualifications.toSetBy { it.id }

        val pendingInvitations = storedInvitations.filter { it.status == InvitationStatus.PENDING }
        val pendingInvitationsNotLinkedToQualification =
            pendingInvitations.filter { it.relatedQualification !in receivedQualifications }

        return pendingInvitationsNotLinkedToQualification.map { it.copy(status = InvitationStatus.CANCELLED) }
    }

    private fun getNewInvitations(
        storedInvitations: List<Invitation>,
        params: DoInvitationsParams
    ): List<Invitation> {

        val newInvitations = mutableListOf<Invitation>()

        val invitationsByQualification = storedInvitations.groupBy { it.relatedQualification }
        val submissionsByIds = params.submissions?.details?.associateBy { it.id } ?: emptyMap()

        params.qualifications.forEach { qualification ->
            val linkedInvitations = invitationsByQualification[qualification.id] ?: emptyList()

            if (pendingInvitationAbsent(linkedInvitations)) {
                val createdInvitation = createInvitation(params, qualification, submissionsByIds)
                newInvitations.add(createdInvitation)
            }
        }

        return newInvitations.toList()
    }

    private fun pendingInvitationAbsent(invitations: List<Invitation>) =
        invitations.none { it.status == InvitationStatus.PENDING }

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

    override fun checkAbsenceActiveInvitations(params: CheckAbsenceActiveInvitationsParams): Validated<Fail> {

        val activeInvitationsFromDb = invitationRepository.findBy(cpid = params.cpid)
            .onFailure { error -> return error.reason.asValidationError() }
            .filter { it.status == InvitationStatus.PENDING }

        return if (activeInvitationsFromDb.isNotEmpty())
            Validated.error(ValidationError.ActiveInvitationsFound(activeInvitationsFromDb.map { it.id }))
        else
            Validated.ok()
    }

    override fun publishInvitations(params: PublishInvitationsParams): Result<PublishInvitationsResult, Fail> {
        val invitationsByStatus = invitationRepository.findBy(cpid = params.cpid)
            .onFailure { return it }
            .groupBy { it.status }

        val pendingInvitations = checkAndGetPendingInvitations(invitationsByStatus, params)
            .onFailure { return it }

        val updatedInvitations = pendingInvitations
            .map { invitation -> invitation.copy(status = InvitationStatus.ACTIVE) }

        val publishedInvitations = updatedInvitations + invitationsByStatus[InvitationStatus.ACTIVE].orEmpty()

        val result = PublishInvitationsResult(
            invitations = publishedInvitations.map { it.convert() }
        )

        invitationRepository.saveAll(params.cpid, updatedInvitations)

        return success(result)
    }

    private fun checkAndGetPendingInvitations(
        invitationsByStatus: Map<InvitationStatus, List<Invitation>>,
        params: PublishInvitationsParams
    ): Result<List<Invitation>, ValidationError.PendingInvitationsNotFoundOnPublishInvitations> {
        val pendingInvitations = invitationsByStatus[InvitationStatus.PENDING].orEmpty()

        when (params.operationType) {
            OperationType.START_SECOND_STAGE ->
                if (pendingInvitations.isEmpty())
                    return failure(ValidationError.PendingInvitationsNotFoundOnPublishInvitations(params.cpid))
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.QUALIFICATION_PROTOCOL,
            OperationType.CREATE_PCR -> Unit
        }

        return pendingInvitations.asSuccess()
    }
}