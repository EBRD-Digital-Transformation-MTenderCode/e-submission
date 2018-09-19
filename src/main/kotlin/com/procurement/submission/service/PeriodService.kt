package com.procurement.submission.service

import com.procurement.submission.dao.PeriodDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.request.PeriodRq
import com.procurement.submission.model.dto.response.BidsSelectionRs
import com.procurement.submission.model.dto.response.CheckPeriodRs
import com.procurement.submission.model.entity.PeriodEntity
import com.procurement.submission.utils.toDate
import com.procurement.submission.utils.toLocal
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

interface PeriodService {

    fun periodValidation(cm: CommandMessage): ResponseDto

    fun savePeriod(cm: CommandMessage): ResponseDto

    fun saveNewPeriod(cm: CommandMessage): ResponseDto

    fun getPeriod(cm: CommandMessage): ResponseDto

    fun checkPeriod(cm: CommandMessage): ResponseDto

    fun checkCurrentDateInPeriod(cpId: String, stage: String, dateTime: LocalDateTime)

    fun getPeriodData(cpId: String, stage: String, dateTime: LocalDateTime): BidsSelectionRs

    fun getPeriodEntity(cpId: String, stage: String): PeriodEntity

    fun save(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime)
}

@Service
class PeriodServiceImpl(private val periodDao: PeriodDao,
                        private val rulesService: RulesService) : PeriodService {

    override fun periodValidation(cm: CommandMessage): ResponseDto {
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)
        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        if (!checkInterval(country, pmd, startDate, endDate)) throw ErrorException(ErrorType.INVALID_PERIOD)
        return ResponseDto(data = "Period is valid.")
    }

    override fun savePeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        val period = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate.toDate(),
                endDate = endDate.toDate())
        periodDao.save(period)
        return ResponseDto(data = Period(period.startDate.toLocal(), period.endDate.toLocal()))
    }

    override fun saveNewPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val oldPeriod = getPeriodEntity(cpId, stage)
        val unsuspendInterval = rulesService.getUnsuspendInterval(country, pmd)
        val endDate = startDate.plusSeconds(unsuspendInterval)
        val newPeriod = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = oldPeriod.startDate,
                endDate = endDate.toDate())
        periodDao.save(newPeriod)
        return ResponseDto(data = Period(newPeriod.startDate.toLocal(), newPeriod.endDate.toLocal()))
    }

    override fun getPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)

        val entity = getPeriodEntity(cpId, stage)
        return ResponseDto(data = Period(entity.startDate.toLocal(), entity.endDate.toLocal()))
    }

    override fun checkPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val operationType = cm.context.operationType ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)
        val startDateRq = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)
        val endDateRq = cm.context.endDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val intervalBefore = rulesService.getIntervalBefore(country, pmd)
        val endDateDb = getPeriodEntity(cpId, stage).endDate.toLocal()
        val checkPoint = endDateDb.minusSeconds(intervalBefore)
        if (operationType == "updateCN") { //((pmd == "OT" && stage == "EV") || (pmd == "RT" && stage == "PS"))
            if (endDateRq < endDateDb) throw ErrorException(ErrorType.INVALID_PERIOD)
        }
        if (operationType == "updateTenderPeriod") { //(pmd == "RT" && (stage == "PQ" || stage == "EV"))
            if (endDateRq <= endDateDb) throw ErrorException(ErrorType.INVALID_PERIOD)
        }
        //1)
        if (startDateRq < checkPoint) {
            if (endDateRq == endDateDb) {
                return getResponse(setExtendedPeriod = false, isPeriodChange = false, newEndDate = endDateDb)
            }
            if (endDateRq > endDateDb) {
                return getResponse(setExtendedPeriod = false, isPeriodChange = true, newEndDate = endDateRq)
            }
        }
        //2)
        if (startDateRq >= checkPoint) {
            if (endDateRq == endDateDb) {
                val newEndDate = startDateRq.plusSeconds(intervalBefore)
                return getResponse(setExtendedPeriod = true, isPeriodChange = true, newEndDate = newEndDate)
            }
            if (endDateRq > endDateDb) {
                val newEndDate = startDateRq.plusSeconds(intervalBefore)
                if (endDateRq <= newEndDate) throw ErrorException(ErrorType.INVALID_PERIOD)
                return getResponse(setExtendedPeriod = false, isPeriodChange = true, newEndDate = endDateRq)
            }
        }
        return getResponse(setExtendedPeriod = false, isPeriodChange = false, newEndDate = endDateDb)
    }

    override fun save(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime) {
        val period = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate.toDate(),
                endDate = endDate.toDate())
        periodDao.save(period)
    }

    override fun checkCurrentDateInPeriod(cpId: String, stage: String, dateTime: LocalDateTime) {
        val periodEntity = getPeriodEntity(cpId, stage)
        val isPeriodValid = (dateTime >= periodEntity.startDate.toLocal() && dateTime <= periodEntity.endDate.toLocal())
        if (!isPeriodValid) throw ErrorException(ErrorType.INVALID_DATE)
    }

    override fun getPeriodData(cpId: String, stage: String, dateTime: LocalDateTime): BidsSelectionRs {
        val tenderPeriodEndDate = getPeriodEntity(cpId, stage).endDate.toLocal()
        val isPeriodExpired = (dateTime >= tenderPeriodEndDate)
        return BidsSelectionRs(
                isPeriodExpired = isPeriodExpired,
                tenderPeriodEndDate = tenderPeriodEndDate,
                bids = setOf())
    }

    override fun getPeriodEntity(cpId: String, stage: String): PeriodEntity {
        return periodDao.getByCpIdAndStage(cpId, stage)
    }

    fun getResponse(setExtendedPeriod: Boolean, isPeriodChange: Boolean, newEndDate: LocalDateTime): ResponseDto {
        return ResponseDto(data = CheckPeriodRs(setExtendedPeriod, isPeriodChange, newEndDate))
    }

    private fun checkInterval(country: String,
                              pmd: String,
                              startDate: LocalDateTime,
                              endDate: LocalDateTime): Boolean {
        val interval = rulesService.getInterval(country, pmd)
        val sec = ChronoUnit.SECONDS.between(startDate, endDate)
        return sec >= interval
    }

    private fun getEntity(cpId: String,
                          stage: String,
                          startDate: Date,
                          endDate: Date): PeriodEntity {
        return PeriodEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate,
                endDate = endDate
        )
    }
}
