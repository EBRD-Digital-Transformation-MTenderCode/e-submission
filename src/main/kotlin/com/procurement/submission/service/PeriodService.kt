package com.procurement.submission.service

import com.procurement.submission.dao.PeriodDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.request.*
import com.procurement.submission.model.dto.response.CheckPeriodEndDateRs
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

    fun checkEndDate(cm: CommandMessage): ResponseDto

    fun getPeriod(cm: CommandMessage): ResponseDto

    fun checkPeriod(cm: CommandMessage): ResponseDto

    fun checkCurrentDateInPeriod(cpId: String, stage: String, dateTime: LocalDateTime)

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
        val dto = toObject(SaveNewPeriodRq::class.java, cm.data)
        val oldPeriod = getPeriodEntity(cpId, stage)
        val tenderInterval = (oldPeriod.endDate.toLocal().second - oldPeriod.startDate.toLocal().second).toLong()
        val startDate = dto.enquiryPeriod.endDate
        val endDate = startDate.plusSeconds(tenderInterval)
        val newPeriod = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate.toDate(),
                endDate = endDate.toDate())
        periodDao.save(newPeriod)
        return ResponseDto(data = Period(startDate, endDate))
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
        val dto = toObject(CheckPeriodRq::class.java, cm.data)
        val setExtendedPeriodRq = dto.setExtendedPeriod ?: throw ErrorException(ErrorType.CONTEXT)
        val isEnquiryPeriodChanged = dto.isEnquiryPeriodChanged ?: throw ErrorException(ErrorType.CONTEXT)
        val enquiryEndDate = dto.enquiryPeriod.endDate
        val tenderEndDateRq = dto.tenderPeriod.endDate

        val periodEntity = getPeriodEntity(cpId, stage)
        val startDateDb = periodEntity.startDate.toLocal()
        val endDateDb = periodEntity.endDate.toLocal()
        if (tenderEndDateRq < endDateDb) throw ErrorException(ErrorType.INVALID_PERIOD)
        //a)
        if (!setExtendedPeriodRq) {
            if (isEnquiryPeriodChanged) {
                val eligibleTenderEndDate = endDateDb.plusSeconds((enquiryEndDate.second - startDateDb.second).toLong())
                if (tenderEndDateRq < eligibleTenderEndDate) throw ErrorException(ErrorType.INVALID_PERIOD)
                return ResponseDto(data = CheckPeriodRs(isTenderPeriodChanged = true, startDate = enquiryEndDate, endDate = tenderEndDateRq))
            } else {
                if (tenderEndDateRq > endDateDb) {
                    return ResponseDto(data = CheckPeriodRs(isTenderPeriodChanged = true, startDate = startDateDb, endDate = tenderEndDateRq))
                } else if (tenderEndDateRq == endDateDb) {
                    return ResponseDto(data = CheckPeriodRs(isTenderPeriodChanged = false, startDate = startDateDb, endDate = endDateDb))
                }
            }
        } else {
            if (tenderEndDateRq > endDateDb) {
                val eligibleTenderEndDate = endDateDb.plusSeconds((enquiryEndDate.second - startDateDb.second).toLong())
                if (tenderEndDateRq < eligibleTenderEndDate) throw ErrorException(ErrorType.INVALID_PERIOD)
                return ResponseDto(data = CheckPeriodRs(isTenderPeriodChanged = true, startDate = enquiryEndDate, endDate = tenderEndDateRq))
            } else if (tenderEndDateRq == endDateDb) {
                val eligibleTenderEndDate = endDateDb.plusSeconds((enquiryEndDate.second - startDateDb.second).toLong())
                return ResponseDto(data = CheckPeriodRs(isTenderPeriodChanged = true, startDate = enquiryEndDate, endDate = eligibleTenderEndDate))
            }
        }
        return ResponseDto(null)
    }

    override fun checkEndDate(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val tenderPeriod = getPeriodEntity(cpId, stage)
        val startDate = tenderPeriod.startDate.toLocal()
        val endDate = tenderPeriod.endDate.toLocal()
        val isTenderPeriodExpired = (dateTime >= endDate)
        return ResponseDto(data = CheckPeriodEndDateRs(isTenderPeriodExpired, startDate, endDate))
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

    override fun getPeriodEntity(cpId: String, stage: String): PeriodEntity {
        return periodDao.getByCpIdAndStage(cpId, stage)
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
