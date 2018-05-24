package com.procurement.submission.service

import com.datastax.driver.core.utils.UUIDs
import com.google.common.base.Strings
import com.procurement.notice.exception.ErrorException
import com.procurement.notice.exception.ErrorType
import com.procurement.notice.model.bpe.ResponseDto
import com.procurement.submission.model.dto.request.LotDto
import com.procurement.submission.model.dto.request.LotsDto
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.model.ocds.*
import com.procurement.submission.repository.BidRepository
import com.procurement.submission.utils.localNowUTC
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Collectors.*

interface BidService {

    fun createBid(cpId: String, stage: String, owner: String, bidDto: Bid): ResponseDto<*>

    fun updateBid(cpId: String, stage: String, token: String, owner: String, bidDto: Bid): ResponseDto<*>

    fun copyBids(cpId: String, newStage: String, previousStage: String, startDate: LocalDateTime, endDate: LocalDateTime, lotsDto: LotsDto): ResponseDto<*>

    fun getPendingBids(cpId: String, stage: String, country: String, pmd: String): ResponseDto<*>

    fun updateStatus(cpId: String, stage: String, country: String, pmd: String, unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto<*>

    fun updateStatusDetails(cpId: String, stage: String, bidId: String, awardStatusDetails: AwardStatusDetails): ResponseDto<*>

    fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto<*>
}

@Service
class BidServiceImpl(private val generationService: GenerationService,
                     private val rulesService: RulesService,
                     private val periodService: PeriodService,
                     private val bidRepository: BidRepository) : BidService {

    override fun createBid(cpId: String,
                           stage: String,
                           owner: String,
                           bidDto: Bid): ResponseDto<*> {
        periodService.checkCurrentDateInPeriod(cpId, stage)
        bidDto.apply {
            validateFieldsForCreate(this)
            checkRelatedLotsInDocuments(this)
            processTenderers(this)
            date = localNowUTC()
            id = generationService.generateTimeBasedUUID().toString()
            status = Status.PENDING
            statusDetails = StatusDetails.EMPTY
        }
        val bids = getBidsFromEntities(bidRepository.findAllByCpIdAndStage(cpId, stage))
        if (!bids.isEmpty()) checkTenderers(bids, bidDto)
        val entity = getNewBidEntity(cpId, stage, owner, bidDto)
        bidRepository.save(entity)
        return getResponseDto(entity.token.toString(), bidDto)
    }

    override fun updateBid(cpId: String,
                           stage: String,
                           token: String,
                           owner: String,
                           bidDto: Bid): ResponseDto<*> {
        periodService.checkCurrentDateInPeriod(cpId, stage)
        validateFieldsForUpdate(bidDto)
        val entity = bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidDto.id!!))
        ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        if (!entity.getOwner().equals(owner)) throw ErrorException(ErrorType.INVALID_OWNER)
        val bid = jsonUtil.toObject(Bid::class.java, entity.getJsonData())
        checkRelatedLotsAndSetDocuments(bid, bidDto)
        if (stage == "EV" && bidDto.value !=
                null) {
            if (Objects.isNull(bidDto.value)) throw ErrorException(ErrorType.VALUE_IS_NULL)
            bid.setValue(bidDto.value)
        }
        bid.setStatus(Status.fromValue(bidDto.statusDetails!!.value()))
        bid.setStatusDetails(StatusDetails.EMPTY)
        bidDto.setDate(dateUtil.localNowUTC())
        entity.setStatus(bid.status!!.value())
        entity.setJsonData(jsonUtil.toJson(bid))
        bidRepository.save(entity)
        return getResponseDto(token, bid)
    }

    override fun copyBids(cpId: String,
                          newStage: String,
                          previousStage: String,
                          startDate: LocalDateTime,
                          endDate: LocalDateTime,
                          lotsDto: LotsDto): ResponseDto {
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, previousStage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        periodService.savePeriod(cpId, newStage, startDate, endDate)
        val validBids = filterForNewStage(bidEntities, lotsDto)
        val newBids = createBidCopy(lotsDto, validBids, newStage)
        bidRepository.saveAll(newBids.keys)
        val bids = ArrayList(newBids.values)
        return ResponseDto(true, null,
                BidsCopyResponseDto(Bids(bids), Period(startDate, endDate)))
    }

    override fun getPendingBids(cpId: String,
                                stage: String,
                                country: String,
                                pmd: String): ResponseDto {
        periodService.checkIsPeriodExpired(cpId, stage)
        val pendingBids = pendingFilter(bidRepository.findAllByCpIdAndStage(cpId, stage))
        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val bids = getBidsFromEntities(pendingBids)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(bids)
        val uniqueLots = getUniqueLots(relatedLotsFromBids)
        val successfulLots = getSuccessfulLots(uniqueLots, minNumberOfBids)
        val successfulBids = getSuccessfulBids(bids, successfulLots)
        return ResponseDto(true, null, BidsSelectionResponseDto(successfulBids))
    }

    override fun updateStatus(cpId: String,
                              stage: String,
                              country: String,
                              pmd: String,
                              unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto {
        /*get all bids entities from db*/
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        /*get all bids from entities*/
        val bids = getBidsFromEntities(bidEntities)
        /*set status WITHDRAWN for bids in INVITED status*/
        val updatedBids = ArrayList<Bid>()
        bids.stream()
                .filter { (_, _, status) -> status == Status.INVITED }
                .forEach { bid ->
                    bid.setDate(dateUtil.localNowUTC())
                    bid.setStatus(Status.WITHDRAWN)
                    bid.setStatusDetails(StatusDetails.EMPTY)
                    updatedBids.add(bid)
                }
        /*set status WITHDRAWN for bids with unsuccessful lots*/
        val lotsStr = collectLots(unsuccessfulLots.lots)
        bids.stream()
                .filter { (_, _, _, _, _, _, _, relatedLots) -> containsAny(relatedLots, lotsStr) }
                .forEach { bid ->
                    bid.setDate(dateUtil.localNowUTC())
                    bid.setStatus(Status.WITHDRAWN)
                    bid.setStatusDetails(StatusDetails.EMPTY)
                    updatedBids.add(bid)
                }
        /*get entities for update*/
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBids)
        /*save updated entities*/
        bidRepository.saveAll(updatedBidEntities)
        /*get tender period from db*/
        val period = periodService.getPeriod(cpId, stage)
        val tenderPeriod = Period(period.getStartDate(), period.getEndDate())

        return ResponseDto(true, null, BidsUpdateStatusResponseDto(tenderPeriod, bids))
    }

    override fun updateStatusDetails(cpId: String,
                                     stage: String,
                                     bidId: String,
                                     awardStatusDetails: AwardStatusDetails): ResponseDto<*> {
        val bidEntity = Optional.ofNullable(
                bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId)))
                .orElseThrow<RuntimeException> { ErrorException(ErrorType.BID_NOT_FOUND) }
        val bid = jsonUtil.toObject(Bid::class.java, bidEntity.getJsonData())
        when (awardStatusDetails) {
            AwardStatusDetails.EMPTY -> {
                bid.setStatusDetails(StatusDetails.EMPTY)
            }
            AwardStatusDetails.ACTIVE -> {
                bid.setStatusDetails(StatusDetails.VALID)
            }
            AwardStatusDetails.UNSUCCESSFUL -> {
                bid.setStatusDetails(StatusDetails.DISQUALIFIED)
            }
        }
        bid.setDate(dateUtil.localNowUTC())
        bidEntity.setJsonData(jsonUtil.toJson(bid))
        bidRepository.save(bidEntity)
        return ResponseDto(true, null, BidsUpdateStatusDetailsResponseDto(getBidUpdate(bid)))
    }

    override fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto<*> {
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bids = getBidsFromEntities(bidEntities)
        //set status from statusDetails
        for (bid in bids) {
            if (bid.status == Status.PENDING && bid.statusDetails != StatusDetails.EMPTY) {
                bid.setDate(dateTime)
                bid.setStatus(Status.fromValue(bid.statusDetails!!.value()))
                bid.setStatusDetails(StatusDetails.EMPTY)
            }
            if (bid.status == Status.PENDING && bid.statusDetails == StatusDetails.EMPTY) {
                bid.setDate(dateTime)
                bid.setStatus(Status.WITHDRAWN)
                bid.setStatusDetails(StatusDetails.EMPTY)
            }
        }
        /*get entities for update*/
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, bids)
        /*save updated entities*/
        bidRepository.saveAll(updatedBidEntities)
        return ResponseDto(true, null, BidsFinalStatusResponseDto(bids))
    }

    private fun validateFieldsForCreate(bidDto: Bid) {
        if (bidDto.id != null) throw ErrorException(ErrorType.ID_NOT_NULL)
    }

    private fun checkRelatedLotsInDocuments(bidDto: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null && bidDto.relatedLots != null) {
                if (!bidDto.relatedLots.containsAll(document.relatedLots)) throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
    }

    private fun checkRelatedLotsAndSetDocuments(bid: Bid, bidDto: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null && bid.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots)) throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
        bidDto.documents?.let { bid.documents = it }
    }

    private fun processTenderers(bidDto: Bid) {
        bidDto.tenderers?.forEach { it.id = it.identifier.scheme + "-" + it.identifier.id }
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }

    private fun checkTenderers(bids: List<Bid>, bidDto: Bid) {
        val dtoRelatedLots = bidDto.relatedLots
        val dtoTenderers = bidDto.tenderers?.asSequence()?.map { it.id }?.toSet()
        bids.forEach { bid ->
            val bidRelatedLots = bid.relatedLots
            val bidTenderers = bid.tenderers?.asSequence()?.map { it.id }?.toSet()
            if (dtoRelatedLots != null && bidRelatedLots != null && dtoTenderers != null && bidTenderers != null) {
                if (bidTenderers.size == bidTenderers.size && bidTenderers.containsAll(dtoTenderers) && bidRelatedLots.containsAll(dtoRelatedLots))
                    throw ErrorException(ErrorType.BID_ALREADY_WITH_LOT)
            }
        }
    }

    private fun validateFieldsForUpdate(bidDto: Bid) {
        if (Strings.isNullOrEmpty(bidDto.id)) throw ErrorException(ErrorType.INVALID_ID)
        if (bidDto.statusDetails == null) throw ErrorException(ErrorType.STATUS_DETAIL_IS_NULL)
    }

    private fun getUpdatedBidEntities(bidEntities: List<BidEntity>, bids: List<Bid>): List<BidEntity> {
        val updatedBidEntities = ArrayList<BidEntity>()
        bids.forEach { bid ->
            bidEntities.stream()
                    .filter { entity -> entity.getBidId().toString().equals(bid.id) }
                    .forEach { entity ->
                        entity.setStatus(bid.status!!.value())
                        entity.setJsonData(jsonUtil.toJson(bid))
                        updatedBidEntities.add(entity)
                    }
        }
        return updatedBidEntities
    }

    private fun getRelatedLotsIdFromBids(bids: List<Bid>): List<String> {
        return bids.stream()
                .flatMap { (_, _, _, _, _, _, _, relatedLots) -> relatedLots!!.stream() }
                .collect<List<String>, Any>(Collectors.toList())
    }

    private fun getUniqueLots(lots: List<String>): Map<String, Long> {
        return lots.stream().collect<Map<String, Long>, Any>(groupingBy<String, String, Any, Long>(Function.identity(), Collectors.counting()))
    }

    private fun getSuccessfulLots(uniqueLots: Map<String, Long>,
                                  minNumberOfBids: Int): List<String> {
        return uniqueLots.entries
                .stream()
                .filter { map -> map.value >= minNumberOfBids }
                .map { map -> map.key }
                .collect<List<String>, Any>(Collectors.toList())
    }

    private fun getSuccessfulBids(bids: List<Bid>, successfulLots: List<String>): List<Bid> {
        val successfulBids = ArrayList<Bid>()
        bids.forEach { bid ->
            bid.relatedLots!!
                    .stream()
                    .filter(Predicate<String> { successfulLots.contains(it) })
                    .map { lot -> bid }
                    .forEach(Consumer<Bid> { successfulBids.add(it) })
        }
        return successfulBids
    }


    private fun getResponseDto(token: String, bid: Bid): ResponseDto<BidResponseDto> {
        return ResponseDto(true, null, BidResponseDto(token, bid.id!!, bid))
    }

    private fun createBidCopy(lotsDto: LotsDto,
                              entityBidMap: Map<BidEntity, Bid>,
                              stage: String): Map<BidEntity, Bid> {
        val lots = collectLots(lotsDto.lots)
        return entityBidMap.entries.stream()
                .filter { e -> containsAny(e.value.relatedLots, lots) }
                .map<Entry<BidEntity, Bid>> { e -> copyBidEntity(e, stage) }
                .collect<Map<BidEntity, Bid>, Any>(toMap<Entry<BidEntity, Bid>, BidEntity, Bid>({ it.key }) { it.value })
    }

    private fun copyBidEntity(entrySet: Entry<BidEntity, Bid>, stage: String): Entry<BidEntity, Bid> {
        val oldBidEntity = entrySet.key
        val newBidEntity = BidEntity()
        newBidEntity.setCpId(oldBidEntity.getCpId())
        newBidEntity.setStage(stage)
        newBidEntity.setBidId(oldBidEntity.getBidId())
        newBidEntity.setToken(oldBidEntity.getToken())
        val newStatus = Status.INVITED
        newBidEntity.setStatus(newStatus.value())
        val dateTimeNow = dateUtil.localNowUTC()
        newBidEntity.setCreatedDate(dateTimeNow)
        val oldBid = entrySet.value
        val newBid = Bid(
                oldBid.id,
                dateTimeNow,
                newStatus,
                StatusDetails.EMPTY,
                oldBid.tenderers, null, null,
                oldBid.relatedLots)
        newBidEntity.setJsonData(jsonUtil.toJson(newBid))
        newBidEntity.setOwner(oldBidEntity.getOwner())
        return AbstractMap.SimpleEntry(newBidEntity, newBid)
    }

    private fun pendingFilter(bids: List<BidEntity>): List<BidEntity> {
        return bids.stream()
                .filter { b -> b.getStatus().equals(Status.PENDING.value()) }
                .collect<List<BidEntity>, Any>(toList())
    }

    private fun filterForNewStage(bidEntities: List<BidEntity>, lotsDto: LotsDto): Map<BidEntity, Bid> {
        val validBids = HashMap<BidEntity, Bid>()
        val lotsID = collectLots(lotsDto.lots)
        bidEntities.forEach { bidEntity ->
            val bid = jsonUtil.toObject(Bid::class.java, bidEntity.getJsonData())
            if (bid.status == Status.VALID && bid.statusDetails == StatusDetails.EMPTY)
                for (lot in bid.relatedLots!!) {
                    if (lotsID.contains(lot))
                        validBids[bidEntity] = bid
                }
        }
        return validBids
    }

    private fun collectLots(lots: List<LotDto>?): Set<String> {
        return lots!!.stream().map(Function<LotDto, String> { it.getId() }).collect<Set<String>, Any>(toSet())
    }

    private fun <T> containsAny(src: Collection<T>?, dest: Collection<T>): Boolean {
        for (value in dest) {
            if (src!!.contains(value)) {
                println(value)
                return true
            }
        }
        return false
    }

    fun getBidUpdate(bid: Bid): BidUpdateDto {
        return BidUpdateDto(bid.id!!,
                bid.date!!,
                bid.status!!,
                bid.statusDetails!!,
                createTenderers(bid.tenderers),
                bid.value!!,
                bid.documents!!,
                bid.relatedLots!!)
    }

    private fun createTenderers(tenderers: List<OrganizationReference>?): List<OrganizationReferenceDto> {
        return tenderers!!.stream()
                .map { (id, name) -> OrganizationReferenceDto(id!!, name) }
                .collect<List<OrganizationReferenceDto>, Any>(toList())
    }

    private fun getNewBidEntity(cpId: String, stage: String, owner: String, bidDto: Bid): BidEntity {
        val bidEntity = BidEntity()
        bidEntity.setCpId(cpId)
        bidEntity.setStage(stage)
        bidEntity.setOwner(owner)
        bidEntity.setStatus(bidDto.status!!.value())
        bidEntity.setBidId(UUID.fromString(bidDto.id!!))
        bidEntity.setToken(UUIDs.random())
        bidEntity.setCreatedDate(bidDto.date)
        bidEntity.setJsonData(jsonUtil.toJson(bidDto))
        return bidEntity
    }
}
