package com.procurement.submission.service

import com.procurement.submission.dao.PeriodDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.Period
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

    fun savePeriod(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime): ResponseDto<*>

    fun saveNewPeriod(cpId: String, stage: String, country: String, pmd: String, startDate: LocalDateTime): ResponseDto<*>

    fun checkCurrentDateInPeriod(cpId: String, stage: String)

    fun checkIsPeriodExpired(cpId: String, stage: String)

    fun getPeriod(cpId: String, stage: String): PeriodEntity

    fun checkPeriod(cpId: String, country: String, pmd: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime): ResponseDto<*>

    fun periodValidation(country: String, pmd: String, startDate: LocalDateTime, endDate: LocalDateTime): ResponseDto<*>
}

@Service
class PeriodServiceImpl(private val periodDao: PeriodDao,
                        private val rulesService: RulesService) : PeriodService {

    override fun savePeriod(cpId: String,
                            stage: String,
                            startDate: LocalDateTime,
                            endDate: LocalDateTime): ResponseDto<*> {
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
                               startDate: LocalDateTime): ResponseDto<*> {
        val oldPeriod = getPeriod(cpId, stage)
        val unsuspendInterval = rulesService.getUnsuspendInterval(country, pmd)
        val endDate = when (country) {
            TEST_PARAM -> startDate.plusMinutes(unsuspendInterval.toLong())
            else -> startDate.plusDays(unsuspendInterval.toLong())
        }
        val newPeriod = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = oldPeriod.startDate,
                endDate = endDate.toDate())
        periodDao.save(newPeriod)
        return ResponseDto(true, null, Period(newPeriod.startDate.toLocal(), newPeriod.endDate.toLocal()))
    }

    override fun checkCurrentDateInPeriod(cpId: String, stage: String) {
        if (!isPeriodValid(cpId, stage)) throw ErrorException(ErrorType.INVALID_DATE)
    }

    override fun checkIsPeriodExpired(cpId: String, stage: String) {
        if (isPeriodValid(cpId, stage)) throw ErrorException(ErrorType.PERIOD_NOT_EXPIRED)
    }

    override fun getPeriod(cpId: String, stage: String): PeriodEntity {
        return periodDao.getByCpIdAndStage(cpId, stage) ?: throw ErrorException(ErrorType.PERIOD_NOT_FOUND)
    }

    override fun checkPeriod(cpId: String,
                             country: String,
                             pmd: String,
                             stage: String,
                             startDate: LocalDateTime,
                             endDate: LocalDateTime): ResponseDto<*> {
        return ResponseDto(true, null,
                CheckPeriodResponseDto(
                        checkInterval(country, pmd, startDate, endDate),
                        isPeriodChange(cpId, stage, startDate, endDate)))
    }

    override fun periodValidation(country: String,
                                  pmd: String,
                                  startDate: LocalDateTime,
                                  endDate: LocalDateTime): ResponseDto<*> {
        if (!checkInterval(country, pmd, startDate, endDate)) throw ErrorException(ErrorType.INVALID_PERIOD)
        return ResponseDto(true, null, "Period is valid.")
    }

    fun isPeriodValid(cpId: String, stage: String): Boolean {
        val localDateTime = localNowUTC()
        val periodEntity = getPeriod(cpId, stage)
        val localDateTimeAfter = localDateTime.isAfter(periodEntity.startDate.toLocal())
                || localDateTime.isEqual(periodEntity.startDate.toLocal())
        val localDateTimeBefore = localDateTime.isBefore(periodEntity.endDate.toLocal())
                || localDateTime.isEqual(periodEntity.endDate.toLocal())
        return localDateTimeAfter && localDateTimeBefore
    }

    fun isPeriodChange(cpId: String,
                       stage: String,
                       startDate: LocalDateTime,
                       endDate: LocalDateTime): Boolean {
        val period = getPeriod(cpId, stage)
        return period.startDate.toLocal() != startDate || period.endDate.toLocal() != endDate
    }

    private fun checkInterval(country: String,
                              pmd: String,
                              startDate: LocalDateTime,
                              endDate: LocalDateTime): Boolean {
        val interval = rulesService.getInterval(country, pmd)
        if (TEST_PARAM == country) {
            val minutes = ChronoUnit.MINUTES.between(startDate, endDate)
            return minutes >= interval
        }
        val days = ChronoUnit.DAYS.between(startDate, endDate)
        return days >= interval
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

    companion object {
        private val TEST_PARAM = "TEST"
    }
}
