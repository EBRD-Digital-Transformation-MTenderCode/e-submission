package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodContext
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodResult
import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.application.params.SetTenderPeriodParams
import com.procurement.submission.application.params.ValidateTenderPeriodParams
import com.procurement.submission.application.repository.period.PeriodRepository
import com.procurement.submission.application.repository.period.model.PeriodEntity
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.bpe.country
import com.procurement.submission.model.dto.bpe.cpid
import com.procurement.submission.model.dto.bpe.ocid
import com.procurement.submission.model.dto.bpe.pmd
import com.procurement.submission.model.dto.bpe.startDate
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.request.CheckPeriodRq
import com.procurement.submission.model.dto.request.CheckPeriodRs
import com.procurement.submission.model.dto.request.PeriodRq
import com.procurement.submission.model.dto.request.SaveNewPeriodRq
import com.procurement.submission.model.dto.response.CheckPeriodEndDateRs
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Duration.between
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class PeriodService(
    private val periodRepository: PeriodRepository,
    private val rulesService: RulesService
) {

    fun periodValidation(cm: CommandMessage): ResponseDto {
        val country = cm.country
        val pmd = cm.pmd
        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        if (!checkInterval(country, pmd, startDate, endDate)) throw ErrorException(ErrorType.INVALID_PERIOD)
        return ResponseDto(data = "Period is valid.")
    }

    fun savePeriod(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid

        val tenderPeriod = toObject(PeriodRq::class.java, cm.data).tenderPeriod
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate

        val entity = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            startDate = startDate,
            endDate = endDate
        )
        periodRepository.save(entity)
            .doOnFail { throw it.exception }

        return ResponseDto(data = Period(entity.startDate, entity.endDate))
    }

    fun saveNewPeriod(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val dto = toObject(SaveNewPeriodRq::class.java, cm.data)
        val oldPeriod = getPeriodEntity(cpid, ocid)
        val tenderInterval = Duration.between(oldPeriod.startDate, oldPeriod.endDate)
        val startDate = dto.enquiryPeriod.endDate
        val endDate = startDate.plus(tenderInterval)
        val newPeriod = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            startDate = startDate,
            endDate = endDate
        )

        periodRepository.save(newPeriod)
            .doOnFail { throw it.exception }
        return ResponseDto(data = Period(startDate, endDate))
    }

    fun getPeriod(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val entity = getPeriodEntity(cpid, ocid)
        return ResponseDto(data = Period(entity.startDate, entity.endDate))
    }

    fun checkPeriod(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val dto = toObject(CheckPeriodRq::class.java, cm.data)
        val setExtendedPeriodRq = dto.setExtendedPeriod
            ?: throw ErrorException(ErrorType.CONTEXT)
        val isEnquiryPeriodChanged = dto.isEnquiryPeriodChanged
            ?: throw ErrorException(
                ErrorType.CONTEXT
            )
        val enquiryEndDateRq = dto.enquiryPeriod.endDate
        val tenderEndDateRq = dto.tenderPeriod.endDate

        val periodEntity = getPeriodEntity(cpid, ocid)
        val startDateDb = periodEntity.startDate
        val endDateDb = periodEntity.endDate
        if (tenderEndDateRq < endDateDb)
            throw ErrorException(ErrorType.INVALID_PERIOD)
        val secBetween = between(startDateDb, enquiryEndDateRq)
        val eligibleTenderEndDate = endDateDb.plus(secBetween)
        //a)
        if (!setExtendedPeriodRq) {
            if (isEnquiryPeriodChanged) {
                if (tenderEndDateRq < eligibleTenderEndDate)
                    throw ErrorException(ErrorType.INVALID_PERIOD)
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
                if (tenderEndDateRq < eligibleTenderEndDate)
                    throw ErrorException(ErrorType.INVALID_PERIOD)
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
        val cpid = cm.cpid
        val ocid = cm.ocid
        val dateTime = cm.startDate

        val tenderPeriod = getPeriodEntity(cpid, ocid)
        val startDate = tenderPeriod.startDate
        val endDate = tenderPeriod.endDate
        val isTenderPeriodExpired = (dateTime >= endDate)
        return ResponseDto(data = CheckPeriodEndDateRs(isTenderPeriodExpired, startDate, endDate))
    }

    fun save(cpid: Cpid, ocid: Ocid, startDate: LocalDateTime, endDate: LocalDateTime) {
        val period = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            startDate = startDate,
            endDate = endDate
        )
        periodRepository.save(period)
            .doOnFail { throw it.exception }
    }

    fun checkCurrentDateInPeriod(cpid: Cpid, ocid: Ocid, dateTime: LocalDateTime) {
        val periodEntity = getPeriodEntity(cpid, ocid)
        val isPeriodValid = (dateTime >= periodEntity.startDate && dateTime <= periodEntity.endDate)
        if (!isPeriodValid) throw ErrorException(ErrorType.INVALID_DATE)
    }

    fun getPeriodEntity(cpid: Cpid, ocid: Ocid): PeriodEntity =
        periodRepository.find(cpid, ocid)
            .onFailure { throw it.reason.exception }
            ?: throw ErrorException(ErrorType.PERIOD_NOT_FOUND)

    private fun checkInterval(
        country: String,
        pmd: ProcurementMethod,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Boolean {
        val interval = rulesService.getInterval(country, pmd.toString())
        val sec = ChronoUnit.SECONDS.between(startDate, endDate) //TODO
        return sec >= interval
    }

    fun validateTenderPeriod(params: ValidateTenderPeriodParams): Validated<Fail> {
        val minimumDuration = rulesService
            .getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        val periodDuration = Duration.between(params.date, params.tender.tenderPeriod.endDate)

        if (periodDuration < minimumDuration)
            return ValidationError.TenderPeriodDurationError(expectedDuration = minimumDuration)
                .asValidationError()

        return Validated.ok()
    }

    fun setTenderPeriod(params: SetTenderPeriodParams): Result<SetTenderPeriodResult, Fail> {
        val entity = PeriodEntity(
            cpid = params.cpid,
            ocid = params.ocid,
            startDate = params.date,
            endDate = params.tender.tenderPeriod.endDate
        )
        periodRepository.save(entity = entity)
            .doOnFail { error -> return error.asFailure() }

        return SetTenderPeriodResult(
            tender = SetTenderPeriodResult.Tender(
                tenderPeriod = SetTenderPeriodResult.Tender.TenderPeriod(
                    startDate = entity.startDate,
                    endDate = entity.endDate
                )
            )
        ).asSuccess()
    }

    fun checkPeriod(params: CheckPeriodParams): Validated<Fail> {
        val tenderPeriod = periodRepository.find(params.cpid, params.ocid)
            .onFailure { return it.reason.asValidationError() }
            ?: return ValidationError.TenderPeriodNotFound(params.cpid, params.ocid).asValidationError()

        if (!params.date.isAfter(tenderPeriod.startDate))
            return ValidationError.ReceivedDatePrecedesStoredStartDate().asValidationError()

        if (!params.date.isBefore(tenderPeriod.endDate))
            return ValidationError.ReceivedDateIsAfterStoredEndDate().asValidationError()

        return Validated.ok()
    }

    fun extendTenderPeriod(context: ExtendTenderPeriodContext): ExtendTenderPeriodResult {
        val tenderPeriod = periodRepository.find(context.cpid, context.ocid)
            .onFailure { throw it.reason.exception }
            ?: throw ErrorException(ErrorType.PERIOD_NOT_FOUND)
        val extensionAfterUnsuspended = rulesService.getExtensionAfterUnsuspended(context.country, context.pmd)
        val newEndDate = context.startDate.plus(extensionAfterUnsuspended)
        val updatedTenderPeriod = tenderPeriod.copy(endDate = newEndDate)

        periodRepository.save(updatedTenderPeriod)

        return ExtendTenderPeriodResult(
            ExtendTenderPeriodResult.TenderPeriod(
                startDate = updatedTenderPeriod.startDate,
                endDate = updatedTenderPeriod.endDate
            )
        )
    }
}
