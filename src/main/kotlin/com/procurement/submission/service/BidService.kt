package com.procurement.submission.service

import com.procurement.submission.application.model.data.BidCreateData
import com.procurement.submission.application.service.AppliedEvaluatedAwardsData
import com.procurement.submission.application.service.ApplyEvaluatedAwardsContext
import com.procurement.submission.application.service.ApplyEvaluatedAwardsData
import com.procurement.submission.application.service.BidCreateContext
import com.procurement.submission.application.service.FinalBidsStatusByLotsContext
import com.procurement.submission.application.service.FinalBidsStatusByLotsData
import com.procurement.submission.application.service.FinalizedBidsStatusByLots
import com.procurement.submission.dao.BidDao
import com.procurement.submission.domain.model.ProcurementMethod
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.isNotUniqueIds
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.exception.ErrorType.BID_ALREADY_WITH_LOT
import com.procurement.submission.exception.ErrorType.BID_NOT_FOUND
import com.procurement.submission.exception.ErrorType.CONTEXT
import com.procurement.submission.exception.ErrorType.INVALID_DATE
import com.procurement.submission.exception.ErrorType.INVALID_DOCS_FOR_UPDATE
import com.procurement.submission.exception.ErrorType.INVALID_DOCS_ID
import com.procurement.submission.exception.ErrorType.INVALID_DOCUMENT_TYPE
import com.procurement.submission.exception.ErrorType.INVALID_OWNER
import com.procurement.submission.exception.ErrorType.INVALID_PERSONES
import com.procurement.submission.exception.ErrorType.INVALID_RELATED_LOT
import com.procurement.submission.exception.ErrorType.INVALID_STATUSES_FOR_UPDATE
import com.procurement.submission.exception.ErrorType.INVALID_TOKEN
import com.procurement.submission.exception.ErrorType.NOT_UNIQUE_IDS
import com.procurement.submission.exception.ErrorType.PERIOD_NOT_EXPIRED
import com.procurement.submission.exception.ErrorType.RELATED_LOTS_MUST_BE_ONE_UNIT
import com.procurement.submission.exception.ErrorType.VALUE_IS_NULL
import com.procurement.submission.model.dto.BidDetails
import com.procurement.submission.model.dto.SetInitialBidsStatusDtoRq
import com.procurement.submission.model.dto.SetInitialBidsStatusDtoRs
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.AccountIdentification
import com.procurement.submission.model.dto.ocds.AdditionalAccountIdentifier
import com.procurement.submission.model.dto.ocds.Address
import com.procurement.submission.model.dto.ocds.AddressDetails
import com.procurement.submission.model.dto.ocds.AwardStatusDetails
import com.procurement.submission.model.dto.ocds.BankAccount
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Bids
import com.procurement.submission.model.dto.ocds.BusinessFunction
import com.procurement.submission.model.dto.ocds.ContactPoint
import com.procurement.submission.model.dto.ocds.CountryDetails
import com.procurement.submission.model.dto.ocds.Details
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Identifier
import com.procurement.submission.model.dto.ocds.IssuedBy
import com.procurement.submission.model.dto.ocds.IssuedThought
import com.procurement.submission.model.dto.ocds.LegalForm
import com.procurement.submission.model.dto.ocds.LocalityDetails
import com.procurement.submission.model.dto.ocds.OrganizationReference
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.ocds.Permit
import com.procurement.submission.model.dto.ocds.PermitDetails
import com.procurement.submission.model.dto.ocds.Persone
import com.procurement.submission.model.dto.ocds.RegionDetails
import com.procurement.submission.model.dto.ocds.Requirement
import com.procurement.submission.model.dto.ocds.RequirementResponse
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails
import com.procurement.submission.model.dto.ocds.ValidityPeriod
import com.procurement.submission.model.dto.ocds.Value
import com.procurement.submission.model.dto.request.BidUpdate
import com.procurement.submission.model.dto.request.BidUpdateDocsRq
import com.procurement.submission.model.dto.request.BidUpdateRq
import com.procurement.submission.model.dto.request.LotDto
import com.procurement.submission.model.dto.request.LotsDto
import com.procurement.submission.model.dto.response.BidCreateResponse
import com.procurement.submission.model.dto.response.BidRs
import com.procurement.submission.model.dto.response.BidsCopyRs
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.containsAny
import com.procurement.submission.utils.localNowUTC
import com.procurement.submission.utils.toDate
import com.procurement.submission.utils.toJson
import com.procurement.submission.utils.toLocal
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Service
class BidService(private val generationService: GenerationService,
                 private val periodService: PeriodService,
                 private val bidDao: BidDao) {

    fun createBid(requestData: BidCreateData, context: BidCreateContext): ResponseDto {

        val bidRequest = requestData.bid
        periodService.checkCurrentDateInPeriod(context.cpid, context.stage, context.startDate)
        checkRelatedLotsInDocuments(bidRequest)
        isOneRelatedLot(bidRequest)
        checkTypeOfDocumentsCreateBid(bidRequest.documents)
        checkTenderers(context.cpid, context.stage, bidRequest)
        checkDocumentsIds(bidRequest.documents)
        checkEntitiesListUniquenessById(bid = bidRequest)           // FReq-1.2.1.6
        checkBusinessFunctionTypeOfDocumentsCreateBid(bidRequest)   // FReq-1.2.1.19
        checkOneAuthority(bid = bidRequest)                         // FReq-1.2.1.20
        checkBusinessFunctionsPeriod(
            bid = bidRequest,
            requestDate = context.startDate
        )                                                           // FReq-1.2.1.39

        val requirementResponses = requirementResponseIdTempToPermanent(bidRequest.requirementResponses)

        val bid = Bid(
                id = generationService.generateBidId().toString(),
                date = context.startDate,
                status = Status.PENDING,
                statusDetails = StatusDetails.EMPTY,
                value = bidRequest.value.let { Value(amount = it.amount, currency = it.currency) },
                documents = bidRequest.documents.toBidEntityDocuments(),
                relatedLots = bidRequest.relatedLots,
                tenderers = bidRequest.tenderers.toBidEntityTenderers(),
                requirementResponses = requirementResponses.toBidEntityRequirementResponse()
        )
        val entity = getEntity(
                bid = bid,
                cpId = context.cpid,
                stage = context.stage,
                owner = context.owner,
                token = generationService.generateRandomUUID(),
                createdDate = context.startDate.toDate(),
                pendingDate = context.startDate.toDate()
        )
        bidDao.save(entity)
        val bidResponse = BidCreateResponse.Bid(
            id = UUID.fromString(bid.id),
            token = entity.token
        )
        return ResponseDto(data = BidCreateResponse(bid = bidResponse))
    }

    fun updateBid(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val bidId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(BidUpdateRq::class.java, cm.data)
        val bidDto = dto.bid

        periodService.checkCurrentDateInPeriod(cpId, stage, dateTime)
        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        checkStatusesBidUpdate(bid)
        checkTypeOfDocumentsUpdateBid(bidDto.documents ?: emptyList())
        validateRelatedLotsOfDocuments(bidDto = bidDto, bid = bid)
        validateValue(stage = stage, bidDto = bidDto)
        bid.apply {
            date = dateTime
            status = Status.PENDING
            documents = updateDocuments(bid.documents, bidDto.documents)
            bidDto.value?.let { value = it }
        }
        entity.jsonData = toJson(bid)
        entity.pendingDate = dateTime.toDate()
        bidDao.save(entity)
        return ResponseDto(data = BidRs(null, bid.id, bid))
    }

    fun copyBids(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val previousStage = cm.context.prevStage ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val endDate = cm.context.endDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val lots = toObject(LotsDto::class.java, cm.data)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, previousStage)
        if (bidEntities.isEmpty()) throw ErrorException(BID_NOT_FOUND)
        periodService.save(cpId, stage, startDate, endDate)
        val mapValidEntityBid = getBidsForNewStageMap(bidEntities, lots)
        val mapCopyEntityBid = getBidsCopyMap(lots, mapValidEntityBid, stage)
        bidDao.saveAll(mapCopyEntityBid.keys.toList())
        val bids = ArrayList(mapCopyEntityBid.values)
        return ResponseDto(data = BidsCopyRs(Bids(bids), Period(startDate, endDate)))
    }

    fun updateBidDocs(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val bidId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(BidUpdateDocsRq::class.java, cm.data)
        val documentsDto = dto.bid.documents
        //VR-4.8.1
        val period = periodService.getPeriodEntity(cpId, stage)
        if (dateTime <= period.endDate.toLocal()) throw ErrorException(PERIOD_NOT_EXPIRED)

        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, "EV", UUID.fromString(bidId))
        if (entity.token.toString() != token) throw ErrorException(INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        //VR-4.8.4
        if ((bid.status != Status.PENDING && bid.statusDetails != StatusDetails.VALID)
                && (bid.status != Status.VALID && bid.statusDetails != StatusDetails.EMPTY)) {
            throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        }
        //VR-4.8.5
        documentsDto.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
        //BR-4.8.2
        val documentsDtoId = documentsDto.asSequence().map { it.id }.toSet()
        val documentsDbId = bid.documents?.asSequence()?.map { it.id }?.toSet() ?: setOf()
        val newDocumentsId = documentsDtoId - documentsDbId
        if (newDocumentsId.isEmpty()) throw ErrorException(INVALID_DOCS_FOR_UPDATE)
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        val documentsDb = bid.documents ?: listOf()
        bid.documents = documentsDb + newDocuments
        entity.jsonData = toJson(bid)
        bidDao.save(entity)
        return ResponseDto(data = BidRs(null, null, bid))
    }

    fun setInitialBidsStatus(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val dto = toObject(SetInitialBidsStatusDtoRq::class.java, cm.data)

        val bidsRsList = arrayListOf<BidDetails>()
        dto.awards.forEach { award ->
            val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(award.relatedBid))
            val bid: Bid = toObject(Bid::class.java, entity.jsonData)
            bid.apply {
                status = Status.PENDING
                statusDetails = StatusDetails.EMPTY
            }
            entity.apply {
                status = Status.PENDING.value()
                jsonData = toJson(bid)
            }
            bidDao.save(entity)
            bidsRsList.add(BidDetails(
                    id = bid.id,
                    status = bid.status,
                    statusDetails = bid.statusDetails
            ))
        }
        return ResponseDto(data = SetInitialBidsStatusDtoRs(bids = bidsRsList))
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
        fun isValid(status: Status, details: StatusDetails) =
            status == Status.PENDING && details == StatusDetails.VALID

        fun isDisqualified(status: Status, details: StatusDetails) =
            status == Status.PENDING && details == StatusDetails.DISQUALIFIED

        fun Bid.updatingStatuses(): Bid = when {
            isValid(this.status, this.statusDetails) -> this.copy(
                status = Status.VALID,
                statusDetails = StatusDetails.EMPTY
            )
            isDisqualified(this.status, this.statusDetails) -> this.copy(
                status = Status.DISQUALIFIED,
                statusDetails = StatusDetails.EMPTY
            )
            else -> throw IllegalStateException("No processing for award with status: '${this.status}' and details: '${this.statusDetails}'.")
        }

        val lotsIds: Set<UUID> = data.lots.asSequence()
            .map { it.id }
            .toSet()

        val stage = getStage(context)
        val updatedBids: Map<Bid, BidEntity> = bidDao.findAllByCpIdAndStage(cpId = context.cpid, stage = stage)
            .asSequence()
            .map { entity ->
                val bid = toObject(Bid::class.java, entity.jsonData)
                bid to entity
            }
            .filter { (bid, _) ->
                bid.relatedLots.any { lotsIds.contains(UUID.fromString(it)) }
            }
            .map { (bid, entity) ->
                val updatedBid = bid.updatingStatuses()

                val updatedEntity = entity.copy(
                    status = updatedBid.status.value,
                    jsonData = toJson(updatedBid)
                )

                updatedBid to updatedEntity
            }
            .toMap()

        bidDao.saveAll(updatedBids.values)

        return FinalizedBidsStatusByLots(
            bids = updatedBids.keys.map { bid ->
                FinalizedBidsStatusByLots.Bid(
                    id = UUID.fromString(bid.id),
                    status = bid.status,
                    statusDetails = bid.statusDetails
                )
            }
        )
    }

    private fun getStage(context: FinalBidsStatusByLotsContext): String = when (context.pmd) {
        ProcurementMethod.OT, ProcurementMethod.TEST_OT,
        ProcurementMethod.SV, ProcurementMethod.TEST_SV,
        ProcurementMethod.MV, ProcurementMethod.TEST_MV -> "EV"

        ProcurementMethod.DA, ProcurementMethod.TEST_DA,
        ProcurementMethod.NP, ProcurementMethod.TEST_NP,
        ProcurementMethod.OP, ProcurementMethod.TEST_OP -> "NP"

        ProcurementMethod.RT, ProcurementMethod.TEST_RT,
        ProcurementMethod.FA, ProcurementMethod.TEST_FA -> throw ErrorException(ErrorType.INVALID_PMD)
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
    ): AppliedEvaluatedAwardsData {
        val relatedBidsByStatuses: Map<UUID, AwardStatusDetails> = data.awards.associate {
            it.relatedBid to it.statusDetails
        }

        val entities: List<BidEntity> = bidDao.findAllByCpIdAndStage(cpId = context.cpid, stage = context.stage)
        val updatedBids = mutableListOf<AppliedEvaluatedAwardsData.Bid>()
        val updatedEntities: List<BidEntity> = mutableListOf<BidEntity>()
            .apply {
                entities.forEach { entity ->
                    val bidId = entity.bidId
                    val statusDetails = relatedBidsByStatuses[bidId]
                    if (statusDetails != null) {
                        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
                        val updatedBid: Bid = bid.updateStatusDetails(statusDetails)
                        updatedBids.add(
                            AppliedEvaluatedAwardsData.Bid(
                                id = bidId,
                                statusDetails = bid.statusDetails
                            )
                        )
                        val updatedEntity = entity.copy(jsonData = toJson(updatedBid))
                        add(updatedEntity)
                    }
                }
            }
        bidDao.saveAll(updatedEntities)

        return AppliedEvaluatedAwardsData(bids = updatedBids)
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
        AwardStatusDetails.ACTIVE -> this.copy(statusDetails = StatusDetails.VALID)
        AwardStatusDetails.UNSUCCESSFUL -> this.copy(statusDetails = StatusDetails.DISQUALIFIED)
        else -> throw ErrorException(
            error = ErrorType.INVALID_STATUS_DETAILS,
            message = "Current status details: '$statusDetails'. Expected status details: [${AwardStatusDetails.ACTIVE}, ${AwardStatusDetails.UNSUCCESSFUL}]"
        )
    }


    private fun updateDocuments(documentsDb: List<Document>?, documentsDto: List<Document>?): List<Document>? {
        return if (documentsDb != null && documentsDb.isNotEmpty()) {
            if (documentsDto != null) {
                val documentsDtoId = documentsDto.asSequence().map { it.id }.toSet()
                if (documentsDtoId.size != documentsDto.size) throw ErrorException(INVALID_DOCS_ID)
                val documentsDbId = documentsDb.asSequence().map { it.id }.toSet()
                val newDocumentsId = documentsDtoId - documentsDbId
                //update
                documentsDb.forEach { document ->
                    document.updateDocument(documentsDto.firstOrNull { it.id == document.id })
                }
                //new
                val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
                documentsDb + newDocuments
            } else {
                documentsDb
            }
        } else {
            documentsDto
        }
    }

    private fun Document.updateDocument(documentDto: Document?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description
            this.relatedLots = documentDto.relatedLots
        }
    }

    private fun checkStatusesBidUpdate(bid: Bid) {
        if (bid.status != Status.PENDING && bid.status != Status.INVITED) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        if (bid.statusDetails != StatusDetails.EMPTY) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
    }

    private fun checkRelatedLotsInDocuments(bidDto: BidCreateData.Bid) {
        bidDto.documents.forEach { document ->
            if (!bidDto.relatedLots.containsAll(document.relatedLots)) throw ErrorException(INVALID_RELATED_LOT)
        }
    }

    private fun checkTypeOfDocumentsCreateBid(documents: List<BidCreateData.Bid.Document>) {
        documents.forEach { document ->
            when (document.documentType) {
                DocumentType.SUBMISSION_DOCUMENTS,
                DocumentType.ELIGIBILITY_DOCUMENTS,
                DocumentType.ILLUSTRATION,
                DocumentType.COMMERCIAL_OFFER,
                DocumentType.QUALIFICATION_DOCUMENTS,
                DocumentType.TECHNICAL_DOCUMENTS -> Unit

                DocumentType.TECHNICAL_PROPOSAL,
                DocumentType.SELECTION_DOCUMENTS -> throw ErrorException(
                    error = INVALID_DOCUMENT_TYPE,
                    message = "Document has invalid type: '${document.documentType.value()}'."
                )
            }
        }
    }
    private fun checkBusinessFunctionTypeOfDocumentsCreateBid(bid: BidCreateData.Bid) {
        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .flatMap { it.documents.asSequence() }
            .forEach { document ->
            when (document.documentType) {
                BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> Unit
            }
        }
    }

    private fun checkOneAuthority(bid: BidCreateData.Bid) {
        fun BusinessFunctionType.validate() {
            when (this) {
                BusinessFunctionType.AUTHORITY,
                BusinessFunctionType.CONTACT_POINT -> Unit
            }
        }

        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .map { it.type }
            .forEach { it.validate() }


        bid.tenderers.forEach {
            val authorityPersones = it.persones
                .map { it.businessFunctions}
                .filter {
                    it.map { it.type }.contains(BusinessFunctionType.AUTHORITY)
                }
                .toList()

            if (authorityPersones.size > 1) {
                throw ErrorException(
                    error = INVALID_PERSONES,
                    message = "Only one person can has 'authority' business function type."
                )
            }
        }
    }

    private fun requirementResponseIdTempToPermanent(requirementResponses: List<BidCreateData.Bid.RequirementResponse>): List<BidCreateData.Bid.RequirementResponse> {
        return requirementResponses.map { requirementResponse ->
            requirementResponse.copy(id = generationService.generateRequirementResponseId().toString())
        }
    }

    private fun checkBusinessFunctionsPeriod(bid : BidCreateData.Bid, requestDate : LocalDateTime ) {
        fun BidCreateData.Bid.Tenderer.Persone.BusinessFunction.Period.validate() {
            if (this.startDate > requestDate) throw ErrorException(
                    error = INVALID_DATE,
                    message = "Period.startDate specified in  business functions cannot be greater than startDate from request."
                )
        }

        bid.tenderers.flatMap { it.persones }
            .flatMap { it.businessFunctions }
            .map { it.period }
            .forEach { it.validate() }
    }


    private fun checkTypeOfDocumentsUpdateBid(documents: List<Document>) {
        documents.forEach { document ->
            when (document.documentType) {
                DocumentType.SUBMISSION_DOCUMENTS,
                DocumentType.ELIGIBILITY_DOCUMENTS,
                DocumentType.ILLUSTRATION,
                DocumentType.COMMERCIAL_OFFER,
                DocumentType.QUALIFICATION_DOCUMENTS,
                DocumentType.TECHNICAL_DOCUMENTS -> Unit

                DocumentType.TECHNICAL_PROPOSAL,
                DocumentType.SELECTION_DOCUMENTS -> throw ErrorException(
                    error = INVALID_DOCUMENT_TYPE,
                    message = "Document has invalid type: '${document.documentType.value()}'."
                )
            }
        }
    }

    private fun checkDocumentsIds(documents: List<BidCreateData.Bid.Document>) {
        if (documents.isNotUniqueIds())
            throw ErrorException(error = INVALID_DOCS_ID, message = "Some documents have the same id.")
    }

    private fun checkEntitiesListUniquenessById(bid: BidCreateData.Bid) {
        bid.tenderers.isNotUniqueIds { throw ErrorException(
            error = NOT_UNIQUE_IDS,
            message = "Some bid.tenderers have the same id.")
        }

        bid.tenderers.map { it.additionalIdentifiers }
            .forEach {
                it.isNotUniqueIds { throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.additionalIdentifiers have the same id.")
                }
            }


        bid.tenderers.flatMap { it.persones }
            .map { it.businessFunctions }
            .forEach {
                it.isNotUniqueIds { throw ErrorException(
                    error = INVALID_DOCS_ID,
                    message = "Some bid.tenderers.persones.businessFunctions have the same id." )
                }
            }


        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .toList()
            .map { it.documents }
            .forEach {
                it.isNotUniqueIds { throw ErrorException(
                    error = INVALID_DOCS_ID,
                    message = "Some bid.tenderers.persones.businessFunctions.documents have the same id.")
                }
            }

        bid.documents.isNotUniqueIds { throw ErrorException(
            error = INVALID_DOCS_ID,
            message = "Some bid.documents have the same id.")
        }
        bid.requirementResponses.isNotUniqueIds { throw ErrorException(
            error = INVALID_DOCS_ID,
            message = "Some bid.requirementResponses have the same id.")
        }

    }

    private fun isOneRelatedLot(bidDto: BidCreateData.Bid) {
        if (bidDto.relatedLots.size > 1) throw ErrorException(RELATED_LOTS_MUST_BE_ONE_UNIT)
    }

    private fun validateRelatedLotsOfDocuments(bidDto: BidUpdate, bid: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }

    private fun checkTenderers(cpId: String, stage: String, bidDto: BidCreateData.Bid) {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isNotEmpty()) {
            val bids = getBidsFromEntities(bidEntities)
            val dtoRelatedLots = bidDto.relatedLots
            val dtoTenderers = bidDto.tenderers.asSequence().map { it.id }.toSet()
            bids.filter { it.status != Status.WITHDRAWN }.forEach { bid ->
                val bidRelatedLots = bid.relatedLots
                val bidTenderers = bid.tenderers.asSequence().map { it.id }.toSet()
                if (bidTenderers.size == dtoTenderers.size &&
                        bidTenderers.containsAll(dtoTenderers) &&
                        bidRelatedLots.containsAll(dtoRelatedLots))
                    throw ErrorException(BID_ALREADY_WITH_LOT)
            }
        }
    }

    fun validateValue(stage: String, bidDto: BidUpdate) {
        if (stage == "EV") {
            if (bidDto.value == null) throw ErrorException(VALUE_IS_NULL)
        }
    }

    private fun getBidsForNewStageMap(bidEntities: List<BidEntity>, lotsDto: LotsDto): Map<BidEntity, Bid> {
        val validBids = HashMap<BidEntity, Bid>()
        val lotsIds = collectLotIds(lotsDto.lots)
        bidEntities.forEach { bidEntity ->
            val bid = toObject(Bid::class.java, bidEntity.jsonData)
            if (bid.status == Status.VALID && bid.statusDetails == StatusDetails.EMPTY)
                bid.relatedLots.forEach {
                    if (lotsIds.contains(it)) validBids[bidEntity] = bid
                }
        }
        return validBids
    }

    private fun getBidsCopyMap(lotsDto: LotsDto,
                               mapEntityBid: Map<BidEntity, Bid>,
                               stage: String): Map<BidEntity, Bid> {
        val bidsCopy = HashMap<BidEntity, Bid>()
        val lotsIds = collectLotIds(lotsDto.lots)
        mapEntityBid.forEach { map ->
            val (entity, bid) = map
            if (bid.relatedLots.containsAny(lotsIds)) {
                val bidCopy = bid.copy(
                        date = localNowUTC(),
                        status = Status.INVITED,
                        statusDetails = StatusDetails.EMPTY,
                        value = null,
                        documents = null)
                val entityCopy = getEntity(
                        bid = bidCopy,
                        cpId = entity.cpId,
                        stage = stage,
                        owner = entity.owner,
                        token = entity.token,
                        createdDate = localNowUTC().toDate(),
                        pendingDate = null)
                bidsCopy[entityCopy] = bidCopy
            }
        }
        return bidsCopy
    }

    private fun collectLotIds(lots: List<LotDto>?): Set<String> {
        return lots?.asSequence()?.map { it.id }?.toSet() ?: setOf()
    }

    private fun getEntity(bid: Bid,
                          cpId: String,
                          stage: String,
                          owner: String,
                          token: UUID,
                          createdDate: Date,
                          pendingDate: Date?): BidEntity {
        return BidEntity(
                cpId = cpId,
                stage = stage,
                owner = owner,
                status = bid.status.value(),
                bidId = UUID.fromString(bid.id),
                token = token,
                createdDate = createdDate,
                pendingDate = pendingDate,
                jsonData = toJson(bid)
        )
    }

    private fun List<BidCreateData.Bid.Document>.toBidEntityDocuments(): List<Document> {
        return this.map { document ->
            Document(
                id = document.id,
                description = document.description,
                title = document.title,
                documentType = document.documentType,
                relatedLots = document.relatedLots.toHashSet()
            )
        }
    }

    private fun List<BidCreateData.Bid.Tenderer>.toBidEntityTenderers(): List<OrganizationReference> {
        return this.map { tenderer ->
            OrganizationReference(
                id = tenderer.id,
                name = tenderer.name,
                identifier = Identifier(
                    id = tenderer.identifier.id,
                    scheme = tenderer.identifier.scheme,
                    legalName = tenderer.identifier.legalName,
                    uri = tenderer.identifier.uri
                ),
                additionalIdentifiers = tenderer.additionalIdentifiers.map { additionalIdentifier ->
                    Identifier(
                        id = additionalIdentifier.id,
                        scheme = additionalIdentifier.scheme,
                        legalName = additionalIdentifier.legalName,
                        uri = additionalIdentifier.uri
                    )
                }.toSet(),
                address = Address(
                    streetAddress = tenderer.address.streetAddress,
                    postalCode = tenderer.address.postalCode,
                    addressDetails = AddressDetails(
                        country = CountryDetails(
                            id = tenderer.address.addressDetails.country.id,
                            scheme = tenderer.address.addressDetails.country.scheme,
                            description = tenderer.address.addressDetails.country.description,
                            uri = tenderer.address.addressDetails.country.uri
                        ),
                        region = RegionDetails(
                            id = tenderer.address.addressDetails.region.id,
                            scheme = tenderer.address.addressDetails.region.scheme,
                            description = tenderer.address.addressDetails.region.description,
                            uri = tenderer.address.addressDetails.region.uri
                        ),
                        locality = LocalityDetails(
                            id = tenderer.address.addressDetails.locality.id,
                            scheme = tenderer.address.addressDetails.locality.scheme,
                            description = tenderer.address.addressDetails.locality.description,
                            uri = tenderer.address.addressDetails.locality.uri
                        )
                    )
                ),
                contactPoint = ContactPoint(
                    name = tenderer.contactPoint.name,
                    email = tenderer.contactPoint.email,
                    telephone = tenderer.contactPoint.telephone,
                    faxNumber = tenderer.contactPoint.faxNumber,
                    url = tenderer.contactPoint.url
                ),
                details = Details(
                    typeOfSupplier = tenderer.details.typeOfSupplier,
                    mainEconomicActivities = tenderer.details.mainEconomicActivities,
                    permits = tenderer.details.permits.map { permit ->
                        Permit(
                            id = permit.id,
                            scheme = permit.scheme,
                            url = permit.url,
                            permitDetails = PermitDetails(
                                issuedBy = IssuedBy(
                                    id = permit.permitDetails.issuedBy.id,
                                    name = permit.permitDetails.issuedBy.name
                                ),
                                issuedThought = IssuedThought(
                                    id = permit.permitDetails.issuedThought.id,
                                    name = permit.permitDetails.issuedThought.name
                                ),
                                validityPeriod = ValidityPeriod(
                                    startDate = permit.permitDetails.validityPeriod.startDate,
                                    endDate = permit.permitDetails.validityPeriod.endDate
                                )
                            )
                        )
                    },
                    scale = tenderer.details.scale,
                    bankAccounts = tenderer.details.bankAccounts.map { bankAccount ->
                        BankAccount(
                            description = bankAccount.description,
                            bankName = bankAccount.bankName,
                            address = Address(
                                streetAddress = bankAccount.address.streetAddress,
                                postalCode = bankAccount.address.postalCode,
                                addressDetails = AddressDetails(
                                    country = CountryDetails(
                                        id = bankAccount.address.addressDetails.country.id,
                                        scheme = bankAccount.address.addressDetails.country.scheme,
                                        description = bankAccount.address.addressDetails.country.description,
                                        uri = bankAccount.address.addressDetails.country.uri
                                    ),
                                    region = RegionDetails(
                                        id = bankAccount.address.addressDetails.region.id,
                                        scheme = bankAccount.address.addressDetails.region.scheme,
                                        description = bankAccount.address.addressDetails.region.description,
                                        uri = bankAccount.address.addressDetails.region.uri
                                    ),
                                    locality = LocalityDetails(
                                        id = bankAccount.address.addressDetails.locality.id,
                                        scheme = bankAccount.address.addressDetails.locality.scheme,
                                        description = bankAccount.address.addressDetails.locality.description,
                                        uri = bankAccount.address.addressDetails.locality.uri
                                    )
                                )
                            ),
                            identifier = BankAccount.Identifier(
                                id = bankAccount.identifier.id,
                                scheme = bankAccount.identifier.scheme
                            ),
                            accountIdentification = AccountIdentification(
                                id = bankAccount.accountIdentification.scheme,
                                scheme = bankAccount.accountIdentification.scheme
                            ),
                            additionalAccountIdentifiers =  bankAccount.additionalAccountIdentifiers.map { accountIdentifier ->
                                AdditionalAccountIdentifier(
                                    id = accountIdentifier.id,
                                    scheme = accountIdentifier.scheme
                                )
                            }
                        )
                    },
                    legalForm = tenderer.details.legalForm?.let {  legalForm ->
                            LegalForm(
                                id = legalForm.id,
                                scheme = legalForm.scheme,
                                description = legalForm.description,
                                uri = legalForm.uri
                            )
                        }
                ),
                persones = tenderer.persones.map { person ->
                    Persone(
                        title = person.title,
                        name = person.name,
                        identifier = Persone.Identifier(
                            id = person.identifier.id,
                            scheme = person.identifier.scheme,
                            uri = person.identifier.uri
                        ),
                        businessFunctions = person.businessFunctions.map { businessFunction ->
                            BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = BusinessFunction.Period(
                                    startDate = businessFunction.period.startDate
                                ),
                                documents = businessFunction.documents.map { document ->
                                    BusinessFunction.Document(
                                        id = document.id,
                                        documentType = document.documentType,
                                        title = document.title,
                                        description = document.description
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    }

    private fun List<BidCreateData.Bid.RequirementResponse>.toBidEntityRequirementResponse(): List<RequirementResponse> {
        return this.map { requirementResponse ->
            RequirementResponse(
                id = requirementResponse.id,
                title = requirementResponse.title,
                description = requirementResponse.description,
                value = requirementResponse.value,
                requirement = Requirement(
                    id = requirementResponse.requirement.id
                ),
                period = Period(
                    startDate = requirementResponse.period.startDate,
                    endDate = requirementResponse.period.endDate
                )
            )
        }
    }

}
