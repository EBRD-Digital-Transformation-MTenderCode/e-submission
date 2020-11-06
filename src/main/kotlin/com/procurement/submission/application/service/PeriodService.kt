package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodContext
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodResult
import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.application.params.SetTenderPeriodParams
import com.procurement.submission.application.params.ValidateTenderPeriodParams
import com.procurement.submission.domain.extension.parseLocalDateTime
import com.procurement.submission.domain.extension.toDate
import com.procurement.submission.domain.extension.toLocal
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.ValidationResult
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.functional.asValidationFailure
import com.procurement.submission.infrastructure.dao.PeriodDao
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodResult
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.request.CheckPeriodRq
import com.procurement.submission.model.dto.request.CheckPeriodRs
import com.procurement.submission.model.dto.request.PeriodRq
import com.procurement.submission.model.dto.request.SaveNewPeriodRq
import com.procurement.submission.model.dto.response.CheckPeriodEndDateRs
import com.procurement.submission.model.entity.PeriodEntity
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class PeriodService(
    private val periodDao: PeriodDao,
    private val rulesService: RulesService
) {

    fun periodValidation(cm: CommandMessage): ResponseDto {
        val country = cm.context.country ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val pmd = cm.context.pmd ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        if (!checkInterval(country, pmd, startDate, endDate)) throw ErrorException(
            ErrorType.INVALID_PERIOD
        )
        return ResponseDto(data = "Period is valid.")
    }

    fun savePeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val stage = cm.context.stage ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        val period = getEntity(
            cpId = cpId,
            stage = stage,
            startDate = startDate.toDate(),
            endDate = endDate.toDate()
        )
        periodDao.save(period)
        return ResponseDto(data = Period(period.startDate.toLocal(), period.endDate.toLocal()))
    }

    fun saveNewPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val stage = cm.context.stage ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val dto = toObject(SaveNewPeriodRq::class.java, cm.data)
        val oldPeriod = getPeriodEntity(cpId, stage)
        val tenderInterval = ChronoUnit.SECONDS.between(oldPeriod.startDate.toLocal(), oldPeriod.endDate.toLocal())
        val startDate = dto.enquiryPeriod.endDate
        val endDate = startDate.plusSeconds(tenderInterval)
        val newPeriod = getEntity(
            cpId = cpId,
            stage = stage,
            startDate = startDate.toDate(),
            endDate = endDate.toDate()
        )
        periodDao.save(newPeriod)
        return ResponseDto(data = Period(startDate, endDate))
    }

    fun getPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val stage = cm.context.stage ?: throw ErrorException(
            ErrorType.CONTEXT
        )

        val entity = getPeriodEntity(cpId, stage)
        return ResponseDto(data = Period(entity.startDate.toLocal(), entity.endDate.toLocal()))
    }

    fun checkPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val stage = cm.context.stage ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val dto = toObject(CheckPeriodRq::class.java, cm.data)
        val setExtendedPeriodRq = dto.setExtendedPeriod ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val isEnquiryPeriodChanged = dto.isEnquiryPeriodChanged ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val enquiryEndDateRq = dto.enquiryPeriod.endDate
        val tenderEndDateRq = dto.tenderPeriod.endDate

        val periodEntity = getPeriodEntity(cpId, stage)
        val startDateDb = periodEntity.startDate.toLocal()
        val endDateDb = periodEntity.endDate.toLocal()
        if (tenderEndDateRq < endDateDb) throw ErrorException(
            ErrorType.INVALID_PERIOD
        )
        val secBetween = ChronoUnit.SECONDS.between(startDateDb, enquiryEndDateRq)
        val eligibleTenderEndDate = endDateDb.plusSeconds(secBetween)
        //a)
        if (!setExtendedPeriodRq) {
            if (isEnquiryPeriodChanged) {
                if (tenderEndDateRq < eligibleTenderEndDate) throw ErrorException(
                    ErrorType.INVALID_PERIOD
                )
                return ResponseDto(
                    data = CheckPeriodRs(
                        isTenderPeriodChanged = true,
                        startDate = enquiryEndDateRq,
                        endDate = tenderEndDateRq
                    )
                )
            } else {
                if (tenderEndDateRq > endDateDb) {
                    return ResponseDto(
                        data = CheckPeriodRs(
                            isTenderPeriodChanged = true,
                            startDate = startDateDb,
                            endDate = tenderEndDateRq
                        )
                    )
                } else if (tenderEndDateRq == endDateDb) {
                    return ResponseDto(
                        data = CheckPeriodRs(
                            isTenderPeriodChanged = false,
                            startDate = startDateDb,
                            endDate = endDateDb
                        )
                    )
                }
            }
        } else {
            if (tenderEndDateRq > endDateDb) {
                if (tenderEndDateRq < eligibleTenderEndDate) throw ErrorException(
                    ErrorType.INVALID_PERIOD
                )
                return ResponseDto(
                    data = CheckPeriodRs(
                        isTenderPeriodChanged = true,
                        startDate = enquiryEndDateRq,
                        endDate = tenderEndDateRq
                    )
                )
            } else if (tenderEndDateRq == endDateDb) {
                return ResponseDto(
                    data = CheckPeriodRs(
                        isTenderPeriodChanged = true,
                        startDate = enquiryEndDateRq,
                        endDate = eligibleTenderEndDate
                    )
                )
            }
        }
        return ResponseDto(null)
    }

    fun checkEndDate(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val stage = cm.context.stage ?: throw ErrorException(
            ErrorType.CONTEXT
        )
        val dateTime = cm.context.startDate?.parseLocalDateTime() ?: throw ErrorException(
            ErrorType.CONTEXT
        )

        val tenderPeriod = getPeriodEntity(cpId, stage)
        val startDate = tenderPeriod.startDate.toLocal()
        val endDate = tenderPeriod.endDate.toLocal()
        val isTenderPeriodExpired = (dateTime >= endDate)
        return ResponseDto(data = CheckPeriodEndDateRs(isTenderPeriodExpired, startDate, endDate))
    }

    fun save(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime) {
        val period = getEntity(
            cpId = cpId,
            stage = stage,
            startDate = startDate.toDate(),
            endDate = endDate.toDate()
        )
        periodDao.save(period)
    }

    fun checkCurrentDateInPeriod(cpId: String, stage: String, dateTime: LocalDateTime) {
        val periodEntity = getPeriodEntity(cpId, stage)
        val isPeriodValid = (dateTime >= periodEntity.startDate.toLocal() && dateTime <= periodEntity.endDate.toLocal())
        if (!isPeriodValid) throw ErrorException(
            ErrorType.INVALID_DATE
        )
    }

    fun getPeriodEntity(cpId: String, stage: String): PeriodEntity {
        return periodDao.getByCpIdAndStage(cpId, stage)
    }

    private fun checkInterval(
        country: String,
        pmd: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Boolean {
        val interval = rulesService.getInterval(country, pmd)
        val sec = ChronoUnit.SECONDS.between(startDate, endDate)
        return sec >= interval
    }

    private fun getEntity(
        cpId: String,
        stage: String,
        startDate: Date,
        endDate: Date
    ): PeriodEntity {
        return PeriodEntity(
            cpId = cpId,
            stage = stage,
            startDate = startDate,
            endDate = endDate
        )
    }

    fun validateTenderPeriod(params: ValidateTenderPeriodParams): ValidationResult<Fail> {
        val minimumDuration = rulesService
            .getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType)
            .doReturn { error -> return error.asValidationFailure() }

        val periodDuration = Duration.between(params.date, params.tender.tenderPeriod.endDate)

        if (periodDuration < minimumDuration)
            return ValidationError.TenderPeriodDurationError(expectedDuration = minimumDuration)
                .asValidationFailure()

        return ValidationResult.ok()
    }

    fun setTenderPeriod(params: SetTenderPeriodParams): Result<SetTenderPeriodResult, Fail> {
        val entity = PeriodEntity(
            cpId = params.cpid.toString(),
            stage = params.ocid.stage.toString(),
            startDate = params.date.toDate(),
            endDate = params.tender.tenderPeriod.endDate.toDate()
        )
        periodDao.trySave(entity = entity)
            .doOnFail { error -> return error.asFailure() }

        return SetTenderPeriodResult(
            tender = SetTenderPeriodResult.Tender(
                tenderPeriod = SetTenderPeriodResult.Tender.TenderPeriod(
                    startDate = entity.startDate.toLocal(),
                    endDate = entity.endDate.toLocal()
                )
            )
        ).asSuccess()
    }

    fun checkPeriod(params: CheckPeriodParams): ValidationResult<Fail> {
        val tenderPeriod = periodDao.tryGetBy(params.cpid, params.ocid.stage)
            .doReturn { error -> return error.asValidationFailure() }
            ?: return ValidationError.TenderPeriodNotFound(params.cpid, params.ocid).asValidationFailure()

        if (!params.date.isAfter(tenderPeriod.startDate.toLocal()))
            return ValidationError.ReceivedDatePrecedesStoredStartDate().asValidationFailure()

        if (!params.date.isBefore(tenderPeriod.endDate.toLocal()))
            return ValidationError.ReceivedDateIsAfterStoredEndDate().asValidationFailure()

        return ValidationResult.ok()
    }

    fun extendTenderPeriod(context: ExtendTenderPeriodContext): ExtendTenderPeriodResult {
        val tenderPeriod = periodDao.getByCpIdAndStage(context.cpid, context.stage)
        val extensionAfterUnsuspended = rulesService.getExtensionAfterUnsuspended(context.country, context.pmd)
        val newEndDate = context.startDate.plus(extensionAfterUnsuspended).toDate()
        val updatedTenderPeriod = tenderPeriod.copy(endDate = newEndDate)

        periodDao.save(updatedTenderPeriod)

        return ExtendTenderPeriodResult(
            ExtendTenderPeriodResult.TenderPeriod(
                startDate = updatedTenderPeriod.startDate.toLocal(),
                endDate = updatedTenderPeriod.endDate.toLocal()
            )
        )
    }
}
