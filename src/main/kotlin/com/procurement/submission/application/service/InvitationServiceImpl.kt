package com.procurement.submission.application.service

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.application.params.CreateInvitationsParams
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.application.params.errors.CreateInvitationsErrors
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.DoInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.PublishInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.convert
import com.procurement.submission.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Result.Companion.failure
import com.procurement.submission.lib.functional.Result.Companion.success
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.model.dto.ocds.Bid
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class InvitationServiceImpl(
    private val invitationRepository: InvitationRepository,
    private val bidRepository: BidRepository,
    private val generationService: GenerationService,
    private val rulesService: RulesService,
    private val transform: Transform
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

    override fun createInvitations(params: CreateInvitationsParams): Result<CreateInvitationsResult, Fail> {
        val receivedBidIds = params.tender.lots.toSetBy { BidId.fromString(it.id.toString()) }

        val bidEntities = bidRepository.findBy(cpid = params.additionalCpid, ocid = params.additionalOcid)
            .onFailure { return it }
            .asSequence()
            .filter { it.bidId in receivedBidIds }
            .map { entity -> entity.bidId to entity }
            .toMap()

        if (!bidEntities.keys.containsAll(receivedBidIds))
            return CreateInvitationsErrors.BidsNotFound(receivedBidIds-bidEntities.keys).asFailure()

        val targetBids = bidEntities.values
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val createdInvitations = targetBids.map { bid -> generateInvitation(bid, params.date) }

        // FR.COM-13.18.6
        invitationRepository.saveAll(params.cpid, createdInvitations)
            .doOnFail { return it.asFailure() }

        return createdInvitations
            .map { CreateInvitationsResult.Invitation.fromDomain(it) }
            .let { CreateInvitationsResult(invitations = it) }
            .asSuccess()
    }

    private fun generateInvitation(bid: Bid, date: LocalDateTime): Invitation {
        return Invitation(
            id = generationService.generateInvitationId(), // FR.COM-13.18.2
            status = InvitationStatus.PENDING,             // FR.COM-13.18.3
            date = date,                                   // FR.COM-13.18.4
            tenderers =  bid.tenderers.map { tenderer -> // FR.COM-13.18.5
                Invitation.Tenderer(
                    id = tenderer.id!!,
                    name = tenderer.name
                )
            },
            relatedQualification = QualificationId.generate()
        )
    }

    private fun checkAndGetPendingInvitations(
        invitationsByStatus: Map<InvitationStatus, List<Invitation>>,
        params: PublishInvitationsParams
    ): Result<List<Invitation>, ValidationError.PendingInvitationsNotFoundOnPublishInvitations> {
        val pendingInvitations = invitationsByStatus[InvitationStatus.PENDING].orEmpty()

        when (params.operationType) {
            OperationType.START_SECOND_STAGE,
            OperationType.COMPLETE_QUALIFICATION ->
                if (pendingInvitations.isEmpty())
                    return failure(ValidationError.PendingInvitationsNotFoundOnPublishInvitations(params.cpid))
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.QUALIFICATION_PROTOCOL,
            OperationType.CREATE_PCR -> Unit
        }

        return pendingInvitations.asSuccess()
    }
}