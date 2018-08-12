package com.procurement.submission.service

import com.procurement.submission.dao.PeriodDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.response.BidsSelectionResponseDto
import com.procurement.submission.model.dto.response.CheckPeriodResponseDto
import com.procurement.submission.model.entity.PeriodEntity
import com.procurement.submission.utils.localNowUTC
import com.procurement.submission.utils.toDate
import com.procurement.submission.utils.toLocal
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

interface PeriodService {

    fun savePeriod(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime): ResponseDto

    fun saveNewPeriod(cpId: String, stage: String, country: String, pmd: String, startDate: LocalDateTime): ResponseDto

    fun getPeriod(cpId: String, stage: String): ResponseDto

    fun checkCurrentDateInPeriod(cpId: String, stage: String)

    fun getPeriodData(cpId: String, stage: String, dateTime: LocalDateTime): BidsSelectionResponseDto

    fun getPeriodEntity(cpId: String, stage: String): PeriodEntity

    fun checkPeriod(cpId: String,
                    country: String,
                    pmd: String,
                    operationType: String,
                    stage: String,
                    requestDate: LocalDateTime,
                    endDateReq: LocalDateTime): ResponseDto

    fun periodValidation(country: String, pmd: String, startDate: LocalDateTime, endDate: LocalDateTime): ResponseDto
}

@Service
class PeriodServiceImpl(private val periodDao: PeriodDao,
                        private val rulesService: RulesService) : PeriodService {

    override fun savePeriod(cpId: String,
                            stage: String,
                            startDate: LocalDateTime,
                            endDate: LocalDateTime): ResponseDto {
        val period = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate.toDate(),
                endDate = endDate.toDate())
        periodDao.save(period)
        return ResponseDto(true, null, Period(period.startDate.toLocal(), period.endDate.toLocal()))
    }

    override fun saveNewPeriod(cpId: String,
                               stage: String,
                               country: String,
                               pmd: String,
                               startDate: LocalDateTime): ResponseDto {
        val oldPeriod = getPeriodEntity(cpId, stage)
        val unsuspendInterval = rulesService.getUnsuspendInterval(country, pmd)
        val endDate = startDate.plusSeconds(unsuspendInterval)
        val newPeriod = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = oldPeriod.startDate,
                endDate = endDate.toDate())
        periodDao.save(newPeriod)
        return ResponseDto(true, null, Period(newPeriod.startDate.toLocal(), newPeriod.endDate.toLocal()))
    }

    override fun getPeriod(cpId: String, stage: String): ResponseDto {
        val entity = getPeriodEntity(cpId, stage)
        return ResponseDto(true, null, Period(entity.startDate.toLocal(), entity.endDate.toLocal()))
    }

    override fun checkCurrentDateInPeriod(cpId: String, stage: String) {
        if (!isPeriodValid(cpId, stage)) throw ErrorException(ErrorType.INVALID_DATE)
    }

    override fun getPeriodData(cpId: String, stage: String, dateTime: LocalDateTime): BidsSelectionResponseDto {
        val tenderPeriodEndDate = getPeriodEntity(cpId, stage).endDate.toLocal()
        val isPeriodExpired = (dateTime >= tenderPeriodEndDate)
        return BidsSelectionResponseDto(isPeriodExpired = isPeriodExpired, tenderPeriodEndDate = tenderPeriodEndDate)
    }

    override fun getPeriodEntity(cpId: String, stage: String): PeriodEntity {
        return periodDao.getByCpIdAndStage(cpId, stage)
    }

    override fun checkPeriod(cpId: String,
                             country: String,
                             pmd: String,
                             operationType: String,
                             stage: String,
                             requestDate: LocalDateTime,
                             endDateReq: LocalDateTime): ResponseDto {

        val intervalBefore = rulesService.getIntervalBefore(country, pmd)
        val endDateDb = getPeriodEntity(cpId, stage).endDate.toLocal()
        val checkPoint = endDateDb.minusSeconds(intervalBefore)
        if (operationType == "updateCN") { //((pmd == "OT" && stage == "EV") || (pmd == "RT" && stage == "PS"))
            if (endDateReq < endDateDb) throw ErrorException(ErrorType.INVALID_PERIOD)
        }
        if (operationType == "updateTenderPeriod") { //(pmd == "RT" && (stage == "PQ" || stage == "EV"))
            if (endDateReq <= endDateDb) throw ErrorException(ErrorType.INVALID_PERIOD)
        }
        //1)
        if (requestDate < checkPoint) {
            if (endDateReq == endDateDb) {
                return getResponse(setExtendedPeriod = false, isPeriodChange = false, newEndDate = endDateDb)
            }
            if (endDateReq > endDateDb) {
                return getResponse(setExtendedPeriod = false, isPeriodChange = true, newEndDate = endDateReq)
            }
        }
        //2)
        if (requestDate >= checkPoint) {
            if (endDateReq == endDateDb) {
                val newEndDate = requestDate.plusSeconds(intervalBefore)
                return getResponse(setExtendedPeriod = true, isPeriodChange = true, newEndDate = newEndDate)
            }
            if (endDateReq > endDateDb) {
                val newEndDate = requestDate.plusSeconds(intervalBefore)
                if (endDateReq <= newEndDate) throw ErrorException(ErrorType.INVALID_PERIOD)
                return getResponse(setExtendedPeriod = false, isPeriodChange = true, newEndDate = endDateReq)
            }
        }
        return getResponse(setExtendedPeriod = false, isPeriodChange = false, newEndDate = endDateDb)
    }

    fun getResponse(setExtendedPeriod: Boolean, isPeriodChange: Boolean, newEndDate: LocalDateTime): ResponseDto {
        return ResponseDto(true, null, CheckPeriodResponseDto(setExtendedPeriod, isPeriodChange, newEndDate))
    }

    override fun periodValidation(country: String,
                                  pmd: String,
                                  startDate: LocalDateTime,
                                  endDate: LocalDateTime): ResponseDto {
        if (!checkInterval(country, pmd, startDate, endDate)) throw ErrorException(ErrorType.INVALID_PERIOD)
        return ResponseDto(true, null, "Period is valid.")
    }

    fun isPeriodValid(cpId: String, stage: String): Boolean {
        val localDateTime = localNowUTC()
        val periodEntity = getPeriodEntity(cpId, stage)
        return localDateTime >= periodEntity.startDate.toLocal() && localDateTime <= periodEntity.endDate.toLocal()
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
