package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.exception.ErrorType.INVALID_DOCS_FOR_UPDATE
import com.procurement.submission.application.exception.ErrorType.INVALID_OWNER
import com.procurement.submission.application.exception.ErrorType.INVALID_RELATED_LOT
import com.procurement.submission.application.exception.ErrorType.INVALID_STATUSES_FOR_UPDATE
import com.procurement.submission.application.exception.ErrorType.INVALID_TOKEN
import com.procurement.submission.application.exception.ErrorType.PERIOD_NOT_EXPIRED
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsContext
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsData
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsResult
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsContext
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsData
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsResult
import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationRequestData
import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationResponseData
import com.procurement.submission.application.model.data.bid.get.GetBidsForEvaluationContext
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsContext
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsData
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsResult
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingContext
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingData
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingResult
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsContext
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsData
import com.procurement.submission.application.model.data.bid.status.FinalizedBidsStatusByLots
import com.procurement.submission.application.params.CheckAccessToBidParams
import com.procurement.submission.application.params.CheckBidStateParams
import com.procurement.submission.application.params.SetStateForBidsParams
import com.procurement.submission.application.params.bid.CreateBidParams
import com.procurement.submission.application.params.bid.FinalizeBidsByAwardsErrors
import com.procurement.submission.application.params.bid.FinalizeBidsByAwardsParams
import com.procurement.submission.application.params.bid.ValidateBidDataParams
import com.procurement.submission.application.params.rules.notEmptyOrBlankRule
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.extension.getDuplicate
import com.procurement.submission.domain.extension.getDuplicated
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.extension.uniqueBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatus
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails.*
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.ProcurementMethodModalities
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.domain.rule.BidStateForSettingRule
import com.procurement.submission.domain.rule.ValidBidStatesRule
import com.procurement.submission.infrastructure.api.v1.CommandMessage
import com.procurement.submission.infrastructure.api.v1.ResponseDto
import com.procurement.submission.infrastructure.api.v1.cpid
import com.procurement.submission.infrastructure.api.v1.ctxId
import com.procurement.submission.infrastructure.api.v1.ocid
import com.procurement.submission.infrastructure.api.v1.owner
import com.procurement.submission.infrastructure.api.v1.startDate
import com.procurement.submission.infrastructure.api.v1.token
import com.procurement.submission.infrastructure.handler.v1.converter.convert
import com.procurement.submission.infrastructure.handler.v1.converter.toBidsForEvaluationResponseData
import com.procurement.submission.infrastructure.handler.v1.model.request.BidUpdateDocsRq
import com.procurement.submission.infrastructure.handler.v1.model.response.BidRs
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.converter.convertToCreateBidResult
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateBidResult
import com.procurement.submission.infrastructure.handler.v2.model.response.FinalizeBidsByAwardsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.SetStateForBidsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.submission.lib.errorIfBlank
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.lib.functional.validate
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.utils.containsAny
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class BidService(
    private val generationService: GenerationService,
    private val rulesService: RulesService,
    private val periodService: PeriodService,
    private val bidRepository: BidRepository,
    private val invitationRepository: InvitationRepository,
    private val transform: Transform,
) {

    fun getBidsForEvaluation(
        requestData: BidsForEvaluationRequestData,
        context: GetBidsForEvaluationContext
    ): BidsForEvaluationResponseData {
        val bidsEntitiesByIds = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == BidStatus.PENDING }
            .associateBy { it.bidId }

        val bidsDb = bidsEntitiesByIds.asSequence()
            .map { (id, entity) ->
                id to toObject(Bid::class.java, entity.jsonData)
            }
            .toMap()

        val bidsByRelatedLot: Map<String, List<Bid>> = bidsDb.values
            .asSequence()
            .flatMap { bid ->
                bid.relatedLots
                    .asSequence()
                    .map { lotId -> lotId to bid }
            }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

        val minNumberOfBids = rulesService.getRulesMinBids(context.country, context.pmd)
        val bidsForEvaluation = requestData.lots
            .asSequence()
            .flatMap { lot ->
                val bids = bidsByRelatedLot[lot.id.toString()] ?: emptyList()
                if (bids.size >= minNumberOfBids)
                    bids.asSequence()
                else
                    emptySequence()
            }
            .associateBy { bid -> UUID.fromString(bid.id) }

        val updatedBidEntities = getBidsForArchive(bids = bidsDb, subtractBids = bidsForEvaluation)
            .asSequence()
            .map { bid -> bid.archive() }
            .map { updatedBid ->
                val entity = bidsEntitiesByIds.getValue(BidId.fromString(updatedBid.id))
                BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = updatedBid
                )
            }
            .toList()
        bidRepository.save(updatedBidEntities)

        return bidsForEvaluation.values.toBidsForEvaluationResponseData()
    }

    private fun getBidsForArchive(bids: Map<UUID, Bid>, subtractBids: Map<UUID, Bid>) =
        bids.asSequence()
            .filter { (id, _) -> id !in subtractBids }
            .map { it.value }

    private fun Bid.archive() = this.copy(statusDetails = BidStatusDetails.ARCHIVED)

    fun openBidsForPublishing(
        context: OpenBidsForPublishingContext,
        data: OpenBidsForPublishingData
    ): OpenBidsForPublishingResult {
        val activeBids: List<Bid> = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == BidStatus.PENDING }
            .map { bidRecord -> toObject(Bid::class.java, bidRecord.jsonData) }
            .toList()

        val bidsForPublishing = when (data.awardCriteriaDetails) {
            AwardCriteriaDetails.AUTOMATED -> {
                val relatedBids: Set<BidId> = data.awards
                    .asSequence()
                    .filter { it.relatedBid != null }
                    .map { it.relatedBid!! }
                    .toSet()

                activeBids.asSequence()
                    .filter { bid -> BidId.fromString(bid.id) in relatedBids }
                    .map { bid ->
                        val bidForPublishing = bid.copy(
                            documents = bid.documents
                                ?.filter { document ->
                                    document.documentType == DocumentType.SUBMISSION_DOCUMENTS
                                        || document.documentType == DocumentType.ELIGIBILITY_DOCUMENTS
                                }
                        )
                        bidForPublishing.convert()
                    }
                    .toList()
            }

            AwardCriteriaDetails.MANUAL -> {
                activeBids.map { bid -> bid.convert() }
            }
        }
        return OpenBidsForPublishingResult(
            bids = bidsForPublishing
        )
    }

    fun updateBidDocs(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val bidId = cm.ctxId
        val dateTime = cm.startDate
        val dto = toObject(BidUpdateDocsRq::class.java, cm.data)
        dto.validateTextAttributes()

        val documentsDto = dto.bid.documents
        //VR-4.8.1
        val period = periodService.getPeriodEntity(cpid, ocid)
        if (dateTime <= period.endDate) throw ErrorException(PERIOD_NOT_EXPIRED)

        val entity = bidRepository.findBy(cpid, ocid, BidId.fromString(bidId))
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)

        //VR-4.8.4
        if ((bid.status == BidStatus.PENDING && bid.statusDetails == BidStatusDetails.VALID) || bid.status == BidStatus.VALID)
            Unit
        else
            throw ErrorException(INVALID_STATUSES_FOR_UPDATE)

        //VR-4.8.5
        documentsDto.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
        //BR-4.8.2
        val documentsDtoId = documentsDto.toSetBy { it.id }
        val documentsDbId = bid.documents?.toSetBy { it.id } ?: setOf()
        val newDocumentsId = documentsDtoId - documentsDbId
        if (newDocumentsId.isEmpty()) throw ErrorException(INVALID_DOCS_FOR_UPDATE)
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        val documentsDb = bid.documents ?: listOf()
        bid.documents = documentsDb + newDocuments

        val updatedBidEntity = BidEntity.Updated(
            cpid = entity.cpid,
            ocid = entity.ocid,
            createdDate = entity.createdDate,
            pendingDate = entity.pendingDate,
            bid = bid
        )
        bidRepository.save(updatedBidEntity)
        return ResponseDto(data = BidRs(null, null, bid))
    }

    private fun BidUpdateDocsRq.validateTextAttributes() {
        bid.documents.forEachIndexed { index, document ->
            document.title.checkForBlank("bid.documents[$index].title")
            document.description.checkForBlank("bid.documents[$index].description")
        }
    }

    private fun String?.checkForBlank(name: String) = this.errorIfBlank {
        ErrorException(
            error = ErrorType.INCORRECT_VALUE_ATTRIBUTE,
            message = "The attribute '$name' is empty or blank."
        )
    }

    /**
     * BR-4.6.6 "status" "statusDetails" (Bid) (set final status by lots)
     *
     * 1. Finds all Bids objects in DB by values of Stage && CPID from the context of Request and saves them as a list to memory;
     * 2. FOR every lot.ID value from list got in Request, eSubmission executes next steps:
     *   a. Selects bids from list (got on step 1) where bid.relatedLots == lots.[id] and saves them as a list to memory;
     *   b. Selects bids from list (got on step 2.a) with bid.status == "pending" && bid.statusDetails == "disqualified" and saves them as a list to memory;
     *   c. FOR every bid from list got on step 2.b:
     *     i.   Sets bid.status == "disqualified" && bid.statusDetails ==  "empty";
     *     ii.  Saves updated Bid to DB;
     *     iii. Returns it for Response as bid.ID && bid.status && bid.statusDetails;
     *   d. Selects bids from list (got on step 2.a) with bid.status == "pending" && bid.statusDetails == "valid" and saves them as a list to memory;
     *   e. FOR every bid from list got on step 2.d:
     *     i.   Sets bid.status == "valid" && bid.statusDetails ==  "empty";
     *     ii.  Saves updated bid to DB;
     *     iii. Returns it for Response as bid.ID && bid.status && bid.statusDetails;
     */
    fun finalBidsStatusByLots(
        context: FinalBidsStatusByLotsContext,
        data: FinalBidsStatusByLotsData
    ): FinalizedBidsStatusByLots {
        fun isValid(status: BidStatus, details: BidStatusDetails) =
            status == BidStatus.PENDING && details == BidStatusDetails.VALID

        fun isDisqualified(status: BidStatus, details: BidStatusDetails) =
            status == BidStatus.PENDING && details == BidStatusDetails.DISQUALIFIED

        fun predicateOfBidStatus(bid: Bid): Boolean = isValid(status = bid.status, details = bid.statusDetails!!)
            || isDisqualified(status = bid.status, details = bid.statusDetails!!)

        fun Bid.updatingStatuses(): Bid = when {
            isValid(this.status, this.statusDetails!!) -> this.copy(
                status = BidStatus.VALID,
                statusDetails = null
            )
            isDisqualified(this.status, this.statusDetails!!) -> this.copy(
                status = BidStatus.DISQUALIFIED,
                statusDetails = null
            )
            else -> throw IllegalStateException("No processing for award with status: '${this.status}' and details: '${this.statusDetails}'.")
        }

        val lotsIds: Set<LotId> = data.lots.toSetBy { it.id }

        val updatedBids: Map<Bid, BidEntity.Updated> = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .map { entity ->
                val bid = toObject(Bid::class.java, entity.jsonData)
                bid to entity
            }
            .filter { (bid, _) ->
                bid.relatedLots.any { lotsIds.contains(LotId.fromString(it)) } && predicateOfBidStatus(bid = bid)
            }
            .map { (bid, entity) ->
                val updatedBid = bid.updatingStatuses()
                val updatedBidEntity = BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = updatedBid
                )
                updatedBid to updatedBidEntity
            }
            .toMap()

        bidRepository.save(updatedBids.values)

        return FinalizedBidsStatusByLots(
            bids = updatedBids.keys
                .map { bid ->
                    FinalizedBidsStatusByLots.Bid(
                        id = UUID.fromString(bid.id),
                        status = bid.status
                    )
                }
        )
    }

    /**
     * CR-10.1.2.1
     *
     * eSubmission executes next steps:
     * 1. forEach award object from Request system executes:
     *   a. Finds appropriate bid object in DB where bid.id == award.relatedBid value from processed award object;
     *   b. Sets bid.statusDetails in object (found before) by rule BR-10.1.2.1;
     *   c. Saves updated Bid to DB;
     *   d. Adds updated Bid to Bids array for response up to next data model:
     *     i.  bid.ID;
     *     ii. bid.statusDetails;
     * 2. Returns bids array for Response;
     *
     */
    fun applyEvaluatedAwards(
        context: ApplyEvaluatedAwardsContext,
        data: ApplyEvaluatedAwardsData
    ): ApplyEvaluatedAwardsResult {
        val relatedBidsByStatuses: Map<UUID, AwardStatusDetails> = data.awards.associate {
            it.relatedBid to it.statusDetails
        }

        val updatedBidEntitiesByBid =
            bidRepository.findBy(context.cpid, context.ocid)
                .orThrow { it.exception }
                .asSequence()
                .filter { entity -> entity.bidId in relatedBidsByStatuses }
                .map { entity ->
                    val statusDetails = relatedBidsByStatuses.getValue(entity.bidId)
                    val updatedBid: Bid = toObject(Bid::class.java, entity.jsonData)
                        .updateStatusDetails(statusDetails)
                    val updatedBidEntity = BidEntity.Updated(
                        cpid = entity.cpid,
                        ocid = entity.ocid,
                        createdDate = entity.createdDate,
                        pendingDate = entity.pendingDate,
                        bid = updatedBid
                    )
                    updatedBid to updatedBidEntity
                }
                .toMap()

        val result = ApplyEvaluatedAwardsResult(
            bids = updatedBidEntitiesByBid.keys
                .map { bid ->
                    ApplyEvaluatedAwardsResult.Bid(
                        id = BidId.fromString(bid.id),
                        statusDetails = bid.statusDetails!!
                    )
                }
        )

        bidRepository.save(updatedBidEntitiesByBid.values)
        return result
    }

    /**
     * BR-10.1.2.1 statusDetails (bid)
     *
     * 1. eEvaluation determines bid.statusDetails depends on award.statusDetails from processed award object of Request:
     *   a. IF [award.statusDetails == "active"] then:
     *      system sets bid.statusDetails == "valid";
     *   b. ELSE [award.statusDetails == "unsuccessful"] then:
     *      system sets bid.statusDetails == "disqualified";
     */
    private fun Bid.updateStatusDetails(statusDetails: AwardStatusDetails): Bid = when (statusDetails) {
        ACTIVE -> this.copy(statusDetails = BidStatusDetails.VALID)
        UNSUCCESSFUL -> this.copy(statusDetails = BidStatusDetails.DISQUALIFIED)

        BASED_ON_HUMAN_DECISION,
        EMPTY,
        PENDING,
        CONSIDERATION,
        AWAITING,
        NO_OFFERS_RECEIVED,
        LOT_CANCELLED -> throw ErrorException(
            error = ErrorType.INVALID_STATUS_DETAILS,
            message = "Current status details: '$statusDetails'. Expected status details: [$ACTIVE, $UNSUCCESSFUL]"
        )
    }

    fun openBidDocs(context: OpenBidDocsContext, data: OpenBidDocsData): OpenBidDocsResult {
        val bidEntity = bidRepository.findBy(context.cpid, context.ocid, data.bidId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bid = toObject(Bid::class.java, bidEntity.jsonData)

        return OpenBidDocsResult(
            bid = OpenBidDocsResult.Bid(
                id = BidId.fromString(bid.id),
                documents = bid.documents
                    ?.map { document ->
                        OpenBidDocsResult.Bid.Document(
                            description = document.description,
                            id = document.id,
                            relatedLots = document.relatedLots
                                ?.map { lotId -> LotId.fromString(lotId) }
                                .orEmpty(),
                            documentType = document.documentType,
                            title = document.title
                        )
                    }
                    .orEmpty()
            )
        )
    }

    fun getBidsByLots(context: GetBidsByLotsContext, data: GetBidsByLotsData): GetBidsByLotsResult {
        val lotsIds = data.lots
            .toSetBy { it.id.toString() }

        val bids = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == BidStatus.PENDING }
            .map { bidEntity -> toObject(Bid::class.java, bidEntity.jsonData) }
            .filter { bid -> bid.status == BidStatus.PENDING && lotsIds.containsAny(bid.relatedLots) }
            .toList()

        return GetBidsByLotsResult(
            bids = bids.map { bid ->
                GetBidsByLotsResult.Bid(
                    id = BidId.fromString(bid.id),
                    documents = bid.documents
                        ?.map { document ->
                            GetBidsByLotsResult.Bid.Document(
                                id = document.id,
                                relatedLots = document.relatedLots
                                    ?.map { relatedLot -> LotId.fromString(relatedLot) }
                                    .orEmpty(),
                                description = document.description,
                                title = document.title,
                                documentType = document.documentType
                            )
                        }
                        .orEmpty(),
                    relatedLots = bid.relatedLots
                        .map { relatedLot -> UUID.fromString(relatedLot) },
                    statusDetails = bid.statusDetails,
                    status = bid.status,
                    tenderers = bid.tenderers
                        .map { tender ->
                            GetBidsByLotsResult.Bid.Tenderer(
                                id = tender.id,
                                name = tender.name,
                                identifier = tender.identifier
                                    .let { identifier ->
                                        GetBidsByLotsResult.Bid.Tenderer.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                address = tender.address
                                    .let { address ->
                                        GetBidsByLotsResult.Bid.Tenderer.Address(
                                            postalCode = address.postalCode,
                                            streetAddress = address.streetAddress,
                                            addressDetails = address.addressDetails
                                                .let { addressDetail ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails(
                                                        country = addressDetail.country
                                                            .let { country ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Country(
                                                                    id = country.id,
                                                                    scheme = country.scheme,
                                                                    description = country.description,
                                                                    uri = country.uri
                                                                )
                                                            },
                                                        locality = addressDetail.locality
                                                            .let { locality ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Locality(
                                                                    id = locality.id,
                                                                    scheme = locality.scheme,
                                                                    description = locality.description,
                                                                    uri = locality.uri
                                                                )
                                                            },
                                                        region = addressDetail.region
                                                            .let { region ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Region(
                                                                    id = region.id,
                                                                    scheme = region.scheme,
                                                                    description = region.description,
                                                                    uri = region.uri
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    },
                                details = tender.details
                                    .let { detail ->
                                        GetBidsByLotsResult.Bid.Tenderer.Details(
                                            typeOfSupplier = detail.typeOfSupplier
                                                ?.let { TypeOfSupplier.creator(it) },
                                            mainEconomicActivities = detail.mainEconomicActivities
                                                ?.map { mainEconomicActivity ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.MainEconomicActivity(
                                                        id = mainEconomicActivity.id,
                                                        description = mainEconomicActivity.description,
                                                        uri = mainEconomicActivity.uri,
                                                        scheme = mainEconomicActivity.scheme
                                                    )
                                                }
                                                .orEmpty(),
                                            scale = Scale.creator(detail.scale),
                                            permits = detail.permits
                                                ?.map { permit ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.Permit(
                                                        id = permit.id,
                                                        scheme = permit.scheme,
                                                        url = permit.url,
                                                        permitDetails = permit.permitDetails
                                                            .let { permitDetail ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = permitDetail.issuedBy
                                                                        .let { issuedBy ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                                id = issuedBy.id,
                                                                                name = issuedBy.name
                                                                            )
                                                                        },
                                                                    issuedThought = permitDetail.issuedThought
                                                                        .let { issuedThought ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                                id = issuedThought.id,
                                                                                name = issuedThought.name
                                                                            )
                                                                        },
                                                                    validityPeriod = permitDetail.validityPeriod
                                                                        .let { validityPeriod ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                                startDate = validityPeriod.startDate,
                                                                                endDate = validityPeriod.endDate
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                }
                                                .orEmpty(),
                                            legalForm = detail.legalForm
                                                ?.let { legalForm ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.LegalForm(
                                                        scheme = legalForm.scheme,
                                                        id = legalForm.id,
                                                        description = legalForm.description,
                                                        uri = legalForm.uri
                                                    )
                                                },
                                            bankAccounts = detail.bankAccounts
                                                ?.map { bankAccount ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount(
                                                        description = bankAccount.description,
                                                        bankName = bankAccount.bankName,
                                                        identifier = bankAccount.identifier
                                                            .let { identifier ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Identifier(
                                                                    id = identifier.id,
                                                                    scheme = identifier.scheme
                                                                )
                                                            },
                                                        address = bankAccount.address
                                                            .let { address ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address(
                                                                    streetAddress = address.streetAddress,
                                                                    postalCode = address.postalCode,
                                                                    addressDetails = address.addressDetails
                                                                        .let { addressDetail ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                country = addressDetail.country
                                                                                    .let { country ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                            id = country.id,
                                                                                            scheme = country.scheme,
                                                                                            description = country.description,
                                                                                            uri = country.uri
                                                                                        )
                                                                                    },
                                                                                locality = addressDetail.locality
                                                                                    .let { locality ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                                            id = locality.id,
                                                                                            scheme = locality.scheme,
                                                                                            description = locality.description,
                                                                                            uri = locality.uri
                                                                                        )
                                                                                    },
                                                                                region = addressDetail.region
                                                                                    .let { region ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                            id = region.id,
                                                                                            scheme = region.scheme,
                                                                                            description = region.description,
                                                                                            uri = region.uri
                                                                                        )
                                                                                    }
                                                                            )
                                                                        }
                                                                )
                                                            },
                                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                            ?.map { additionalAccountIdentifier ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                    id = additionalAccountIdentifier.id,
                                                                    scheme = additionalAccountIdentifier.scheme
                                                                )
                                                            }
                                                            .orEmpty(),
                                                        accountIdentification = bankAccount.accountIdentification
                                                            .let { accountIdentification ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                                    scheme = accountIdentification.scheme,
                                                                    id = accountIdentification.id
                                                                )
                                                            }
                                                    )
                                                }
                                                .orEmpty()
                                        )
                                    },
                                contactPoint = tender.contactPoint
                                    .let { contactPoint ->
                                        GetBidsByLotsResult.Bid.Tenderer.ContactPoint(
                                            name = contactPoint.name,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            email = contactPoint.email!!,
                                            url = contactPoint.url
                                        )
                                    },
                                additionalIdentifiers = tender.additionalIdentifiers
                                    ?.map { additionalIdentifier ->
                                        GetBidsByLotsResult.Bid.Tenderer.AdditionalIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    }
                                    .orEmpty(),
                                persones = tender.persones
                                    ?.map { person ->
                                        GetBidsByLotsResult.Bid.Tenderer.Persone(
                                            identifier = person.identifier
                                                .let { identifier ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Persone.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme,
                                                        uri = identifier.uri
                                                    )
                                                },
                                            name = person.name,
                                            title = person.title,
                                            businessFunctions = person.businessFunctions
                                                .map { businessFunction ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction(
                                                        id = businessFunction.id,
                                                        type = businessFunction.type,
                                                        jobTitle = businessFunction.jobTitle,
                                                        documents = businessFunction.documents
                                                            ?.map { document ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction.Document(
                                                                    id = document.id,
                                                                    documentType = document.documentType,
                                                                    title = document.title,
                                                                    description = document.description
                                                                )
                                                            }
                                                            .orEmpty(),
                                                        period = businessFunction.period
                                                            .let { period ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                                    startDate = period.startDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }
                                    .orEmpty()
                            )
                        },
                    requirementResponses = bid.requirementResponses
                        ?.map { requirementResponse ->
                            GetBidsByLotsResult.Bid.RequirementResponse(
                                id = requirementResponse.id,
                                value = requirementResponse.value,
                                period = requirementResponse.period
                                    ?.let { period ->
                                        GetBidsByLotsResult.Bid.RequirementResponse.Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                requirement = GetBidsByLotsResult.Bid.RequirementResponse.Requirement(
                                    id = requirementResponse.requirement.id
                                ),
                                relatedTenderer = requirementResponse.relatedTenderer?.let { relatedTenderer ->
                                    GetBidsByLotsResult.Bid.RequirementResponse.RelatedTenderer(
                                        id = relatedTenderer.id,
                                        name = relatedTenderer.name
                                    )
                                },
                                evidences = requirementResponse.evidences?.map { evidence ->
                                    GetBidsByLotsResult.Bid.RequirementResponse.Evidence(
                                        id = evidence.id,
                                        title = evidence.title,
                                        description = evidence.description,
                                        relatedDocument = evidence.relatedDocument?.let { relatedDocument ->
                                            GetBidsByLotsResult.Bid.RequirementResponse.Evidence.RelatedDocument(
                                                id = relatedDocument.id
                                            )
                                        }
                                    )
                                }
                            )
                        }
                        .orEmpty(),
                    date = bid.date,
                    value = bid.value!!
                )
            }
        )
    }

    fun validateBidData(params: ValidateBidDataParams): Validated<Fail> {
        validateTextAttributes(params).onFailure { return it.reason.asValidationError() }
        checkBidsValue(params).onFailure { return it.reason.asValidationError() }
        checkTenderers(params).onFailure { return it.reason.asValidationError() }
        checkDocuments(params).onFailure { return it.reason.asValidationError() }
        checkItems(params).onFailure { return it.reason.asValidationError() }
        checkBid(params.bids).onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun validateTextAttributes(params: ValidateBidDataParams): Validated<DataErrors.Validation> {
        params.bids.details.forEachIndexed { idx, bid ->
            val bidPath = "bids.details[$idx]"
            bid.requirementResponses.forEachIndexed { idx, requirementResponse ->
                requirementResponse.id
                    .validate(notEmptyOrBlankRule("$bidPath.requirementResponses[$idx].id"))
                    .onFailure { return it.reason.asValidationError() }
            }

            bid.tenderers.forEachIndexed { tendererIdx, tenderer ->
                val tendererPath = "$bidPath.tenderers[$tendererIdx]"
                tenderer.name.validate(notEmptyOrBlankRule("$tendererPath.name")).onFailure { return it.reason.asValidationError() }
                tenderer.identifier.apply {
                    val identifierPath = "$tendererPath.identifier"

                    id.validate(notEmptyOrBlankRule("$identifierPath.id"))
                        .onFailure { return it.reason.asValidationError() }

                    scheme.validate(notEmptyOrBlankRule("$identifierPath.scheme"))
                        .onFailure { return it.reason.asValidationError() }

                    legalName.validate(notEmptyOrBlankRule("$identifierPath.legalName"))
                        .onFailure { return it.reason.asValidationError() }

                    uri?.validate(notEmptyOrBlankRule("$identifierPath.uri"))?.onFailure { return it.reason.asValidationError() }
                }
                tenderer.additionalIdentifiers.forEachIndexed { idx, identifier ->
                    val additionalIdentifiersPath = "$tendererPath.additionalIdentifiers[$idx]"

                    identifier.apply {
                        id.validate(notEmptyOrBlankRule("$additionalIdentifiersPath.id"))
                            .onFailure { return it.reason.asValidationError() }

                        scheme.validate(notEmptyOrBlankRule("$additionalIdentifiersPath.scheme"))
                            .onFailure { return it.reason.asValidationError() }

                        legalName.validate(notEmptyOrBlankRule("$additionalIdentifiersPath.legalName"))
                            .onFailure { return it.reason.asValidationError() }

                        uri?.validate(notEmptyOrBlankRule("$additionalIdentifiersPath.uri"))
                            ?.onFailure { return it.reason.asValidationError() }
                    }
                }
                tenderer.address.apply {
                    val addressPath = "$tendererPath.address"

                    streetAddress.validate(notEmptyOrBlankRule("$addressPath.streetAddress"))
                        .onFailure { return it.reason.asValidationError() }

                    postalCode?.validate(notEmptyOrBlankRule("$addressPath.postalCode"))
                        ?.onFailure { return it.reason.asValidationError() }

                    addressDetails.locality.let { locality ->
                        val addressLocalityPath = "$addressPath.addressDetails.locality"

                        locality.id.validate(notEmptyOrBlankRule("$addressLocalityPath.id"))
                            .onFailure { return it.reason.asValidationError() }

                        locality.scheme.validate(notEmptyOrBlankRule("$addressLocalityPath.scheme"))
                            .onFailure { return it.reason.asValidationError() }

                        locality.description.validate(notEmptyOrBlankRule("$addressLocalityPath.description"))
                            .onFailure { return it.reason.asValidationError() }
                    }
                }
                tenderer.contactPoint.apply {
                    val contactPointPath = "$tendererPath.contactPoint"

                    name.validate(notEmptyOrBlankRule("$contactPointPath.name"))
                        .onFailure { return it.reason.asValidationError() }

                    email.validate(notEmptyOrBlankRule("$contactPointPath.email"))
                        .onFailure { return it.reason.asValidationError() }

                    faxNumber?.validate(notEmptyOrBlankRule("$contactPointPath.faxNumber"))
                        ?.onFailure { return it.reason.asValidationError() }

                    telephone.validate(notEmptyOrBlankRule("$contactPointPath.telephone"))
                        .onFailure { return it.reason.asValidationError() }

                    url?.validate(notEmptyOrBlankRule("$contactPointPath.url"))
                        ?.onFailure { return it.reason.asValidationError() }
                }
                tenderer.persones.forEachIndexed { idx, persone ->
                    val personPath = "$tendererPath.persones[$idx]"

                    persone.name.validate(notEmptyOrBlankRule("$personPath.name"))
                        .onFailure { return it.reason.asValidationError() }

                    persone.identifier.apply {
                        val identifierPath = "$personPath.identifier"

                        id.validate(notEmptyOrBlankRule("$identifierPath.id"))
                            .onFailure { return it.reason.asValidationError() }

                        scheme.validate(notEmptyOrBlankRule("$identifierPath.scheme"))
                            .onFailure { return it.reason.asValidationError() }

                        uri?.validate(notEmptyOrBlankRule("$identifierPath.uri"))
                            ?.onFailure { return it.reason.asValidationError() }
                    }
                    persone.businessFunctions.forEachIndexed { bfIdx, businessFunction ->
                        val businessFunctionPath = "$personPath.businessFunctions[$bfIdx]"

                        businessFunction.jobTitle.validate(notEmptyOrBlankRule("$businessFunctionPath.jobTitle"))
                            .onFailure { return it.reason.asValidationError() }

                        businessFunction.documents.forEachIndexed { docIdx, document ->
                            val documentsPath = "$businessFunctionPath.documents[$docIdx]"

                            document.description?.validate(notEmptyOrBlankRule("$documentsPath.description"))
                                ?.onFailure { return it.reason.asValidationError() }
                        }
                    }
                }
                tenderer.details.apply {
                    val detailsPath = "$tendererPath.details"
                    mainEconomicActivities.forEachIndexed { idx, mainEconomicActivity ->
                        val mainEconomicActivityPath = "$detailsPath.mainEconomicActivities[$idx]"
                        mainEconomicActivity.apply {
                            id.validate(notEmptyOrBlankRule("$mainEconomicActivityPath.id"))
                                .onFailure { return it.reason.asValidationError() }

                            scheme.validate(notEmptyOrBlankRule("$mainEconomicActivityPath.scheme"))
                                .onFailure { return it.reason.asValidationError() }

                            description.validate(notEmptyOrBlankRule("$mainEconomicActivityPath.description"))
                                .onFailure { return it.reason.asValidationError() }

                            uri?.validate(notEmptyOrBlankRule("$mainEconomicActivityPath.uri"))
                                ?.onFailure { return it.reason.asValidationError() }
                        }
                    }
                    permits.forEachIndexed { idx, permit ->
                        val permitPath = "$detailsPath.permits[$idx]"

                        permit.id.validate(notEmptyOrBlankRule("$permitPath.id"))
                            .onFailure { return it.reason.asValidationError() }

                        permit.scheme.validate(notEmptyOrBlankRule("$permitPath.scheme"))
                            .onFailure { return it.reason.asValidationError() }

                        permit.url?.validate(notEmptyOrBlankRule("$permitPath.url"))
                            ?.onFailure { return it.reason.asValidationError() }

                        permit.permitDetails.apply {
                            val permitDetailsPath = "$permitPath.permitDetails"
                            issuedBy.apply {
                                val issuedByPath = "$permitDetailsPath.issuedBy"

                                id.validate(notEmptyOrBlankRule("$issuedByPath.id"))
                                    .onFailure { return it.reason.asValidationError() }

                                name.validate(notEmptyOrBlankRule("$issuedByPath.name"))
                                    .onFailure { return it.reason.asValidationError() }
                            }
                            issuedThought.apply {
                                val issuedThoughtPath = "$permitDetailsPath.issuedThought"

                                id.validate(notEmptyOrBlankRule("$issuedThoughtPath.id"))
                                    .onFailure { return it.reason.asValidationError() }

                                name.validate(notEmptyOrBlankRule("$issuedThoughtPath.name"))
                                    .onFailure { return it.reason.asValidationError() }
                            }
                        }
                    }
                    bankAccounts.forEachIndexed { idx, bankAccount ->
                        val bankAccountPath = "$detailsPath.bankAccounts[$idx]"

                        bankAccount.description.validate(notEmptyOrBlankRule("$bankAccountPath.description"))
                            .onFailure { return it.reason.asValidationError() }

                        bankAccount.bankName.validate(notEmptyOrBlankRule("$bankAccountPath.bankName"))
                            .onFailure { return it.reason.asValidationError() }

                        bankAccount.address.apply {
                            val addressPath = "$bankAccountPath.address"

                            streetAddress.validate(notEmptyOrBlankRule("$addressPath.streetAddress"))
                                .onFailure { return it.reason.asValidationError() }

                            postalCode?.validate(notEmptyOrBlankRule("$addressPath.postalCode"))
                                ?.onFailure { return it.reason.asValidationError() }

                            addressDetails.locality.apply {
                                val localityPath = "$bankAccountPath.addressDetails.locality"
                                description.validate(notEmptyOrBlankRule("$localityPath.description")).onFailure { return it.reason.asValidationError() }
                            }
                        }
                        bankAccount.identifier.apply {
                            val identifierPath = "$bankAccountPath.identifier"

                            id.validate(notEmptyOrBlankRule("$identifierPath.id"))
                                .onFailure { return it.reason.asValidationError() }

                            scheme.validate(notEmptyOrBlankRule("$identifierPath.scheme"))
                                .onFailure { return it.reason.asValidationError() }
                        }
                        bankAccount.accountIdentification.apply {
                            val accountIdentificationPath = "$bankAccountPath.accountIdentification"

                            id.validate(notEmptyOrBlankRule("$accountIdentificationPath.id"))
                                .onFailure { return it.reason.asValidationError() }

                            scheme.validate(notEmptyOrBlankRule("$accountIdentificationPath.scheme"))
                                .onFailure { return it.reason.asValidationError() }
                        }
                        bankAccount.additionalAccountIdentifiers.forEachIndexed { idx, additionalAccountIdentifier ->
                            val additionalAccountIdentifierPath = "$bankAccountPath.additionalAccountIdentifiers[$idx]"

                            additionalAccountIdentifier.id.validate(notEmptyOrBlankRule("$additionalAccountIdentifierPath.id"))
                                .onFailure { return it.reason.asValidationError() }

                            additionalAccountIdentifier.scheme.validate(notEmptyOrBlankRule("$additionalAccountIdentifierPath.scheme"))
                                .onFailure { return it.reason.asValidationError() }
                        }
                    }
                    legalForm?.apply {
                        val legalFormPath = "$detailsPath.legalForm"

                        id.validate(notEmptyOrBlankRule("$legalFormPath.id"))
                            .onFailure { return it.reason.asValidationError() }

                        scheme.validate(notEmptyOrBlankRule("$legalFormPath.scheme"))
                            .onFailure { return it.reason.asValidationError() }

                        description.validate(notEmptyOrBlankRule("$legalFormPath.description"))
                            .onFailure { return it.reason.asValidationError() }

                        uri?.validate(notEmptyOrBlankRule("$legalFormPath.uri"))
                            ?.onFailure { return it.reason.asValidationError() }
                    }
                }
            }
            bid.documents.forEachIndexed { docIdx, document ->
                val documentPath = "$bidPath.documents[$docIdx]"

                document.description?.validate(notEmptyOrBlankRule("$documentPath.description"))
                    ?.onFailure { return it.reason.asValidationError() }

                document.title.validate(notEmptyOrBlankRule("$documentPath.title"))
                    .onFailure { return it.reason.asValidationError() }
            }
        }

        return Validated.ok()
    }

    private fun checkBidsValue(params: ValidateBidDataParams): Validated<Fail.Error> {
        val requiresElectronicCatalogue = params.tender.procurementMethodModalities
            .any { it == ProcurementMethodModalities.REQUIRES_ELECTRONIC_CATALOGUE }

        val pmd = params.pmd

        if ((pmd.isOpen() || pmd.isSelective()) || (pmd.isFrameworkAgreement() && !requiresElectronicCatalogue)) {
            val bid = params.bids.details.first()
            val value = bid.value ?: return ValidationError.MissingBidValue(bid.id).asValidationError()

            if (value.amount.value <= BigDecimal.ZERO)
                return ValidationError.InvalidBidAmount(bid.id).asValidationError()

            if (value.currency != params.tender.value.currency)
                return ValidationError.InvalidBidCurrency(bid.id).asValidationError()
        }

        return Validated.ok()
    }

    private fun ProcurementMethod.isOpen() =
        when(this) {
            ProcurementMethod.MV, ProcurementMethod.TEST_MV,
            ProcurementMethod.OT, ProcurementMethod.TEST_OT,
            ProcurementMethod.SV, ProcurementMethod.TEST_SV -> true

            ProcurementMethod.CD, ProcurementMethod.TEST_CD,
            ProcurementMethod.CF, ProcurementMethod.TEST_CF,
            ProcurementMethod.DA, ProcurementMethod.TEST_DA,
            ProcurementMethod.DC, ProcurementMethod.TEST_DC,
            ProcurementMethod.FA, ProcurementMethod.TEST_FA,
            ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
            ProcurementMethod.IP, ProcurementMethod.TEST_IP,
            ProcurementMethod.NP, ProcurementMethod.TEST_NP,
            ProcurementMethod.OF, ProcurementMethod.TEST_OF,
            ProcurementMethod.OP, ProcurementMethod.TEST_OP,
            ProcurementMethod.RFQ, ProcurementMethod.TEST_RFQ,
            ProcurementMethod.RT, ProcurementMethod.TEST_RT -> false
        }

    private fun ProcurementMethod.isSelective() =
        when(this) {
            ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
            ProcurementMethod.RFQ, ProcurementMethod.TEST_RFQ,
            ProcurementMethod.RT, ProcurementMethod.TEST_RT -> true

            ProcurementMethod.CD, ProcurementMethod.TEST_CD,
            ProcurementMethod.CF, ProcurementMethod.TEST_CF,
            ProcurementMethod.DA, ProcurementMethod.TEST_DA,
            ProcurementMethod.DC, ProcurementMethod.TEST_DC,
            ProcurementMethod.FA, ProcurementMethod.TEST_FA,
            ProcurementMethod.IP, ProcurementMethod.TEST_IP,
            ProcurementMethod.MV, ProcurementMethod.TEST_MV,
            ProcurementMethod.NP, ProcurementMethod.TEST_NP,
            ProcurementMethod.OF, ProcurementMethod.TEST_OF,
            ProcurementMethod.OP, ProcurementMethod.TEST_OP,
            ProcurementMethod.OT, ProcurementMethod.TEST_OT,
            ProcurementMethod.SV, ProcurementMethod.TEST_SV -> false
        }

    private fun ProcurementMethod.isFrameworkAgreement() =
        when(this) {
            ProcurementMethod.OF, ProcurementMethod.TEST_OF,
            ProcurementMethod.CF, ProcurementMethod.TEST_CF -> true

            ProcurementMethod.CD, ProcurementMethod.TEST_CD,
            ProcurementMethod.DA, ProcurementMethod.TEST_DA,
            ProcurementMethod.DC, ProcurementMethod.TEST_DC,
            ProcurementMethod.FA, ProcurementMethod.TEST_FA,
            ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
            ProcurementMethod.IP, ProcurementMethod.TEST_IP,
            ProcurementMethod.MV, ProcurementMethod.TEST_MV,
            ProcurementMethod.NP, ProcurementMethod.TEST_NP,
            ProcurementMethod.OP, ProcurementMethod.TEST_OP,
            ProcurementMethod.OT, ProcurementMethod.TEST_OT,
            ProcurementMethod.RT, ProcurementMethod.TEST_RT,
            ProcurementMethod.SV, ProcurementMethod.TEST_SV,
            ProcurementMethod.RFQ, ProcurementMethod.TEST_RFQ -> false
        }

    private fun checkTenderers(params: ValidateBidDataParams): Validated<Fail> {
        val tenderers = params.bids.details.first().tenderers
        val duplicateTenderer = tenderers.getDuplicate { it.id }
        if (duplicateTenderer != null)
            return ValidationError.DuplicateTenderers(duplicateTenderer.id).asValidationError()

        if (params.pmd.isSelective() || params.pmd.isFrameworkAgreement()) {
            checkForActiveInvitations(params)
                .onFailure { return it.reason.asValidationError() }
        }

        checkForDuplicatePersonBusinessFunctions(tenderers)
            .onFailure { return it.reason.asValidationError() }

        checkForDuplicatePersonDocuments(tenderers)
            .onFailure { return it.reason.asValidationError() }

        checkWhetherSchemesMatchByCountry(params)
            .onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun checkWhetherSchemesMatchByCountry(params: ValidateBidDataParams): Validated<ValidationError> {
        val identifierSchemesByCountry = params.bids.details.asSequence()
            .flatMap { it.tenderers }
            .associateBy(
                valueTransform = { it.identifier.scheme },
                keySelector = { it.address.addressDetails.country.id }
            )

        val registrationSchemesByCountry = params.mdm.registrationSchemes.associateBy(
            keySelector = { it.country },
            valueTransform = { it.schemes.toSet() }
        )

        identifierSchemesByCountry.forEach { schemeByCountry ->
            val identifierScheme = schemeByCountry.value
            val country = schemeByCountry.key
            if (!schemesMatch(registrationSchemesByCountry, country, identifierScheme))
                return ValidationError.SchemeMismatchByCountry(identifierScheme, country).asValidationError()
        }

        return Validated.ok()
    }

    private fun schemesMatch(
        registrationSchemesByCountry: Map<String, Set<String>>,
        country: String,
        identifierScheme: String
    ) = registrationSchemesByCountry[country]?.contains(identifierScheme) ?: false

    private fun checkForActiveInvitations(params: ValidateBidDataParams): Validated<Fail> {
        val activeInvitations = invitationRepository.findBy(params.cpid)
            .onFailure { return it.reason.asValidationError() }
            .filter { invitation -> invitation.status == InvitationStatus.ACTIVE }

        val groupsOfTenderers = activeInvitations
            .asSequence()
            .map { invitation -> invitation.tenderers.toSetBy { it.id } }
            .toSet()

        val receivedTenderers = params.bids.details.first().tenderers.toSetBy { it.id }

        if (receivedTenderers !in groupsOfTenderers)
            return ValidationError.ActiveInvitationNotFound().asValidationError()

        return Validated.ok()
    }

    private fun checkForDuplicatePersonDocuments(tenderers: List<ValidateBidDataParams.Bids.Detail.Tenderer>): Validated<Fail.Error> {
        tenderers.flatMap { tenderer -> tenderer.persones }
            .map { person ->
                val duplicateDocuments = person.businessFunctions
                    .flatMap { businessFunction -> businessFunction.documents }
                    .getDuplicate { it.id }

                if (duplicateDocuments != null)
                    return ValidationError.DuplicatePersonDocuments(person.id, duplicateDocuments.id)
                        .asValidationError()
            }
        return Validated.ok()
    }

    private fun checkForDuplicatePersonBusinessFunctions(tenderers: List<ValidateBidDataParams.Bids.Detail.Tenderer>): Validated<Fail.Error> {
        tenderers.flatMap { tenderer -> tenderer.persones }
            .map { person ->
                val duplicateBusinessFunction = person.businessFunctions.getDuplicate { it.id }
                if (duplicateBusinessFunction != null)
                    return ValidationError.DuplicatePersonBusinessFunctions(person.id, duplicateBusinessFunction.id)
                        .asValidationError()
            }

        return Validated.ok()
    }

    private fun checkDocuments(params: ValidateBidDataParams): Validated<Fail.Error> {
        val bid = params.bids.details.first()
        checkForDuplicateBidDocument(bid).onFailure { return it.reason.asValidationError() }
        checkRelatedLots(bid).onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun checkForDuplicateBidDocument(bid: ValidateBidDataParams.Bids.Detail): Validated<Fail.Error> {
        val documents = bid.documents

        if (documents.isNotEmpty()) {
            val duplicateDocument = documents.getDuplicate { it.id }
            if (duplicateDocument != null)
                return ValidationError.DuplicateDocuments(bid.id, duplicateDocument.id).asValidationError()
        }
        return Validated.ok()
    }

    private fun checkRelatedLots(bid: ValidateBidDataParams.Bids.Detail): Validated<Fail.Error> {
        val documentsRelatedLots = bid.documents.flatMap { it.relatedLots }.toSet()
        if (documentsRelatedLots.isNotEmpty()) {
            if (documentsRelatedLots != bid.relatedLots.toSet())
                return ValidationError.InvalidRelatedLots().asValidationError()
        }
        return Validated.ok()
    }

    private fun checkItems(params: ValidateBidDataParams): Validated<Fail> {
        val requiresElectronicCatalogue = params.tender.procurementMethodModalities
            .any { it == ProcurementMethodModalities.REQUIRES_ELECTRONIC_CATALOGUE }

        if (requiresElectronicCatalogue) {
            val bid = params.bids.details.first()

            if (bid.items.isEmpty())
                return ValidationError.MissingItems().asValidationError()

            val duplicateItem = bid.items.getDuplicate { it.id }
            if (duplicateItem != null)
                return ValidationError.DuplicateItems(bid.id, duplicateItem.id).asValidationError()

            checkTenderItemsContainBidItems(bid, params).onFailure { return it.reason.asValidationError() }
            checkItemValue(bid, params).onFailure { return it.reason.asValidationError() }
            checkBidAndTenderUnitEquality(bid, params).onFailure { return it.reason.asValidationError() }
        }

        return Validated.ok()
    }

    private fun checkBid(bids: ValidateBidDataParams.Bids): Validated<Fail> {
        checkBidRelation(bids).onFailure { return it.reason.asValidationError() }

        bids.details
            .flatMap { it.requirementResponses }
            .run {
                checkRequirementResponses(this)
                    .onFailure { return it.reason.asValidationError() }

                checkDocuments(this, bids.details.flatMap { it.documents })
                    .onFailure { return it.reason.asValidationError() }
            }

        bids.details.forEach { bid ->
            if (bid.tenderers.size > 1) {
                bid.requirementResponses
                    .filter { it.relatedTenderer == null }
                    .forEach { return ValidationError.ValidateBidData.MissingRelatedTenderer(it.id).asValidationError() }
                }
            }

        return Validated.ok()
    }

    /**
     * Check bid related only to one lot
     */
    private fun checkBidRelation(bids: ValidateBidDataParams.Bids): Validated<Fail> {
        bids.details.forEach { bid ->
            if (bid.relatedLots.size != 1)
                return ValidationError.ValidateBidData.InvalidBidRelationToLot(bid.id).asValidationError()
        }
        return Validated.ok()
    }

    private fun checkDocuments(
        responses: List<ValidateBidDataParams.Bids.Detail.RequirementResponse>,
        documents: List<ValidateBidDataParams.Bids.Detail.Document>
    ): Validated<Fail> {
        val receivedDocuments = documents.toSetBy { it.id }

        responses
            .flatMap { it.evidences }
            .mapNotNull { it.relatedDocument?.id }
            .filter { specifiedDocument -> specifiedDocument !in receivedDocuments }
            .forEach { missingDocument -> return ValidationError.ValidateBidData.MissingDocuments(missingDocument).asValidationError() }

        return Validated.ok()
    }

    private fun checkRequirementResponses(responses: List<ValidateBidDataParams.Bids.Detail.RequirementResponse>): Validated<Fail> {

        /**
         *  Check Ids uniqueness
         */
        if (!responses.uniqueBy { it.id }) {
            val duplicatedIds = responses.getDuplicated { it.id }
            return ValidationError.ValidateBidData.DuplicatedRequirementResponseIds(duplicatedIds).asValidationError()
        }

        /**
         * Check tenderer answered only once per requirement
         */
        responses
            .groupBy(keySelector = { it.relatedTenderer?.id }, valueTransform = { it.requirement })
            .filter { (_, requirements) -> !requirements.uniqueBy { it.id } }
            .forEach { (tenderer, requirements) ->
                val duplicatedResponse = requirements.getDuplicated { it.id }.first()
                return ValidationError.ValidateBidData.TooManyRequirementResponse(tenderer, duplicatedResponse).asValidationError()
            }

        val allReceivedEvidences = responses.flatMap { it.evidences }
        if (!allReceivedEvidences.uniqueBy { it.id }) {
            val duplicatedIds = responses.getDuplicated { it.id }
            return ValidationError.ValidateBidData.DuplicatedEvidencesIds(duplicatedIds).asValidationError()
        }

        responses.forEach { response ->
            response.period?.let { period ->
                if (!period.endDate.isAfter(LocalDateTime.now()))
                    return ValidationError.ValidateBidData.InvalidPeriodEndDate(response.id).asValidationError()

                if (!period.startDate.isBefore(period.endDate))
                    return ValidationError.ValidateBidData.InvalidPeriod(response.id).asValidationError()
            }
        }

        return Validated.ok()
    }

    private fun checkItemValue(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        bid.items.map { item ->
            val value = item.unit.value
            if (value.amount.value <= BigDecimal.ZERO)
                return ValidationError.InvalidItemAmount(item.id).asValidationError()

            if (value.currency != params.tender.value.currency)
                return ValidationError.InvalidItemCurrency(item.id).asValidationError()
        }

        return Validated.ok()
    }

    private fun checkTenderItemsContainBidItems(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        val bidItems = bid.items.toSetBy { it.id }
        val tenderItems = params.tender.items.toSetBy { it.id }
        if(!tenderItems.containsAll(bidItems))
            return ValidationError.InvalidItems().asValidationError()

        return Validated.ok()
    }

    private fun checkBidAndTenderUnitEquality(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        val bidUnits = bid.items.toSetBy { it.unit.id }
        val tenderUnits = params.tender.items.toSetBy { it.unit.id }
        if (bidUnits != tenderUnits)
            return ValidationError.InvalidUnits().asValidationError()

        return Validated.ok()
    }

    fun createBid(params: CreateBidParams): Result<CreateBidResult, Fail> {
        val bidEntities = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it }
            .asSequence()
            .map { entity -> entity.bidId to entity }
            .toMap()

        val bids = bidEntities.values
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val receivedBid = params.bids.details.first()
        val receivedTenderers = receivedBid
            .tenderers
            .toSetBy { it.id }


        val bidToWithdraw = bids.firstOrNull { bid ->
            containsActiveBidByReceivedTenderersAndLot(bid, receivedTenderers, receivedBid)
        }

        val updatedBid = bidToWithdraw?.withdrawBid()
        val updatedBidEntity = updatedBid
            ?.let { bid ->
                val entity = bidEntities.getValue(BidId.fromString(bid.id))
                BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = bid
                )
            }

        val createdBid = receivedBid
            .convert(params.date)

        val createdBidEntity = BidEntity.New(
            cpid = params.cpid,
            ocid = params.ocid,
            token = generationService.generateToken(),
            owner = params.owner,
            createdDate = params.date,
            pendingDate = params.date,
            bid = createdBid
        )

        bidRepository.save(listOfNotNull(updatedBidEntity, createdBidEntity))

        return createdBid.convertToCreateBidResult(createdBidEntity.token).asSuccess()
    }

    fun finalizeBidsByAwards(params: FinalizeBidsByAwardsParams): Result<FinalizeBidsByAwardsResult, Fail> {
        val receivedAwardsById = params.awards.associateBy { it.relatedBid }
        val bidEntities = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it }
            .asSequence()
            .filter { it.bidId in receivedAwardsById }
            .map { entity -> entity.bidId to entity }
            .toMap()

        if (!bidEntities.keys.containsAll(receivedAwardsById.keys))
            return FinalizeBidsByAwardsErrors.BidsNotFound(receivedAwardsById.keys-bidEntities.keys).asFailure()

        val targetBids = bidEntities.values
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val finalizedBids = targetBids.map { bid ->
            val receivedAward = receivedAwardsById.getValue(BidId.fromString(bid.id))
            bid.copy(status = defineFinalizingStatus(receivedAward))
        }

        val finalizedBidsEntities = finalizedBids.map { bid ->
            val entity = bidEntities.getValue(BidId.fromString(bid.id))
            BidEntity.Updated(
                cpid = entity.cpid,
                ocid = entity.ocid,
                createdDate = entity.createdDate,
                pendingDate = entity.pendingDate,
                bid = bid
            )
        }

        bidRepository.save(finalizedBidsEntities).doOnFail { return it.asFailure() }

        return finalizedBids
            .map { FinalizeBidsByAwardsResult.Bids.Detail.fromDomain(it) }
            .let { FinalizeBidsByAwardsResult(bids = FinalizeBidsByAwardsResult.Bids(it)) }
            .asSuccess()
    }

    private fun defineFinalizingStatus(award: FinalizeBidsByAwardsParams.Award): BidStatus =
        when {
            award.status == AwardStatus.ACTIVE && award.statusDetails == BASED_ON_HUMAN_DECISION -> BidStatus.VALID
            award.status == AwardStatus.UNSUCCESSFUL && award.statusDetails == BASED_ON_HUMAN_DECISION -> BidStatus.DISQUALIFIED
            else -> throw IllegalArgumentException()
        }

    private fun containsActiveBidByReceivedTenderersAndLot(
        storedBid: Bid,
        receivedTenderers: Set<String>,
        receivedBid: CreateBidParams.Bids.Detail
    ): Boolean {
        val storedBidTenderers = storedBid.tenderers.toSetBy { it.id }
        val storedLot = storedBid.relatedLots.first()
        val receivedLot = receivedBid.relatedLots.first()
        return storedBid.isActive()
            && storedBidTenderers == receivedTenderers
            && storedLot == receivedLot
    }

    fun Bid.isActive(): Boolean = status == BidStatus.PENDING

    fun Bid.withdrawBid() = copy(status = BidStatus.WITHDRAWN, statusDetails = null)

    fun checkAccessToBid(params: CheckAccessToBidParams): Validated<Fail> {
        val bidId = params.bids.details.first().id
        val bidEntity = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid, id = bidId)
            .onFailure { return it.reason.asValidationError() }
            ?: return ValidationError.CheckAccessToBid.BidNotFound(bidId).asValidationError()

        if (bidEntity.token != params.token)
            return ValidationError.CheckAccessToBid.TokenDoesNotMatch().asValidationError()

        if (bidEntity.owner != params.owner)
            return ValidationError.CheckAccessToBid.OwnerDoesNotMatch().asValidationError()

        return Validated.ok()
    }

    fun checkBidState(params: CheckBidStateParams): Validated<Fail> {
        val bidId = params.bids.details.first().id
        val bidEntity = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid, id = bidId)
            .onFailure { return it.reason.asValidationError() }
            ?: return ValidationError.CheckBidState.BidNotFound(bidId).asValidationError()

        val bid = transform.tryDeserialization(bidEntity.jsonData, Bid::class.java)
            .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
            .onFailure { return it.reason.asValidationError() }

        val validStates = rulesService.getValidStates(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        return if (bidStateIsValid(bid, validStates))
            Validated.ok()
        else Validated.error(ValidationError.CheckBidState.InvalidStateOfBid(bidId))
    }

    private fun bidStateIsValid(bid: Bid, validStates: ValidBidStatesRule): Boolean =
        validStates.contains(bid.status, bid.statusDetails)

    fun setStateForBids(params: SetStateForBidsParams): Result<SetStateForBidsResult, Fail> {
        val bidIds = params.bids.details.map { it.id }
        val entities = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid, ids = bidIds)
            .onFailure { return it }
        val missingBidIds = bidIds.toSet() - entities.toSetBy { it.bidId }

        if (missingBidIds.isNotEmpty())
            return ValidationError.SetStateForBids.BidsNotFound(missingBidIds).asFailure()

        val stateToSet = rulesService.getStateForSetting(params.country, params.pmd, params.operationType)
            .onFailure { return it }

        val updatedEntities = getUpdatedBidEntities(entities, stateToSet)
            .onFailure { return it }

        bidRepository.save(updatedEntities)
            .doOnFail { return it.asFailure() }

        return updatedEntities.map { entity ->
            SetStateForBidsResult.Bids.Detail(
                id = entity.bid.id,
                status = entity.bid.status
            )
        }
            .let { SetStateForBidsResult(SetStateForBidsResult.Bids(it)) }
            .asSuccess()
    }

    private fun getUpdatedBidEntities(
        entities: List<BidEntity.Record>,
        stateToSet: BidStateForSettingRule
    ): Result<List<BidEntity.Updated>, Fail> {
        val bids = entities.mapResult { entity -> transform.tryDeserialization(entity.jsonData, Bid::class.java) }
            .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
            .onFailure { return it.reason.asFailure() }

        val entitiesByBidId = entities.associateBy { it.bidId.toString() }

        val updatedBidsById = bids.map { bid ->
            bid.copy(
                status = stateToSet.status,
                statusDetails = stateToSet.statusDetails ?: bid.statusDetails
            )
        }.associateBy { it.id }

       return updatedBidsById.map { (bidId, bid) ->
            val correspondingEntity = entitiesByBidId.getValue(bidId)

            BidEntity.Updated(
                cpid = correspondingEntity.cpid,
                ocid = correspondingEntity.ocid,
                createdDate = correspondingEntity.createdDate,
                pendingDate = correspondingEntity.pendingDate,
                bid = bid
            )
        }.asSuccess()
    }
}
