package com.procurement.submission.service

import com.procurement.submission.dao.BidDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.exception.ErrorType.*
import com.procurement.submission.model.dto.BidDetails
import com.procurement.submission.model.dto.SetInitialBidsStatusDtoRq
import com.procurement.submission.model.dto.SetInitialBidsStatusDtoRs
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.model.dto.request.*
import com.procurement.submission.model.dto.response.BidRs
import com.procurement.submission.model.dto.response.BidsCopyRs
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class BidService(private val generationService: GenerationService,
                 private val periodService: PeriodService,
                 private val bidDao: BidDao) {

    fun createBid(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(BidCreateRq::class.java, cm.data)
        val bidDto = dto.bid

        periodService.checkCurrentDateInPeriod(cpId, stage, dateTime)
        checkRelatedLotsInDocuments(bidDto)
        processTenderers(bidDto)
        isOneRelatedLot(bidDto)
//        checkTypeOfDocuments(bidDto.documents)
        checkTenderers(cpId, stage, bidDto)
        val bid = Bid(
                id = generationService.getTimeBasedUUID(),
                date = dateTime,
                status = Status.PENDING,
                statusDetails = StatusDetails.EMPTY,
                value = bidDto.value,
                documents = bidDto.documents,
                relatedLots = bidDto.relatedLots,
                tenderers = bidDto.tenderers
        )
        val entity = getEntity(
                bid = bid,
                cpId = cpId,
                stage = stage,
                owner = owner,
                token = generationService.generateRandomUUID(),
                createdDate = dateTime.toDate(),
                pendingDate = dateTime.toDate()
        )
        bidDao.save(entity)
        return ResponseDto(data = BidRs(entity.token.toString(), bid.id, bid))
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
//        checkTypeOfDocuments(bidDto.documents)
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
        var stage = cm.context.stage ?: throw ErrorException(CONTEXT)
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
        //if (bid.status != Status.PENDING) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        //if (bid.statusDetails != StatusDetails.VALID) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        if (bid.status != Status.VALID) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        if (bid.statusDetails != StatusDetails.EMPTY) throw ErrorException(INVALID_STATUSES_FOR_UPDATE)

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

    private fun checkRelatedLotsInDocuments(bidDto: BidCreate) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null) {
                if (!bidDto.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
    }

    private fun checkTypeOfDocuments(documents: List<Document>?) {
        if (documents != null) {
            documents.asSequence().firstOrNull { it.documentType == DocumentType.SUBMISSION_DOCUMENTS }
                    ?: throw ErrorException(CREATE_BID_DOCUMENTS_SUBMISSION)
        }
    }

    private fun isOneRelatedLot(bidDto: BidCreate) {
        if (bidDto.relatedLots.size > 1) throw ErrorException(RELATED_LOTS_MUST_BE_ONE_UNIT)
    }

    private fun validateRelatedLotsOfDocuments(bidDto: BidUpdate, bid: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
    }

    private fun processTenderers(bidDto: BidCreate) {
        bidDto.tenderers.forEach { it.id = it.identifier.scheme + "-" + it.identifier.id }
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }

    private fun checkTenderers(cpId: String, stage: String, bidDto: BidCreate) {
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

}
