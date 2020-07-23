package com.procurement.submission.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.params.ValidateTenderPeriodParams
import com.procurement.submission.domain.extension.format
import com.procurement.submission.domain.functional.ValidationResult
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.dao.PeriodDao
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

internal class PeriodServiceTest {

    companion object {
        val CPID = Cpid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892") ?: throw RuntimeException()
        val OCID = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896") ?: throw RuntimeException()
        private const val COUNTRY = "MD"
        private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT)
        private val DATE = LocalDateTime.parse("2020-02-10T08:49:55Z", FORMATTER)
        val MINIMUM_DURATION = Duration.ofDays(10).seconds
    }

    val rulesService: RulesService = mock()
    val periodDao: PeriodDao = mock()
    val periodService = PeriodService(periodDao, rulesService)

    @Nested
    inner class ValidateTenderPeriod {

        @Test
        fun tenderPeriodExceedsMinimumDuration_success() {
            val durationLongerThanMinimum = MINIMUM_DURATION + 1
            val endDate = DATE.plusSeconds(durationLongerThanMinimum)
            val params: ValidateTenderPeriodParams = getParams(date = DATE, endDate = endDate)
            whenever(rulesService.getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType))
                .thenReturn(Duration.ofSeconds(MINIMUM_DURATION).asSuccess())
            val actual = periodService.validateTenderPeriod(params)

            assertTrue(actual is ValidationResult.Ok)
        }

        @Test
        fun tenderPeriodEqualsMinimumDuration_success() {
            val endDate = DATE.plusSeconds(MINIMUM_DURATION)
            val params: ValidateTenderPeriodParams = getParams(date = DATE, endDate = endDate)
            whenever(rulesService.getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType))
                .thenReturn(Duration.ofSeconds(MINIMUM_DURATION).asSuccess())
            val actual = periodService.validateTenderPeriod(params)

            assertTrue(actual is ValidationResult.Ok)
        }

        @Test
        fun tenderPeriodLessThanMinimumDuration_fail() {
            val durationLessThanMinimum = MINIMUM_DURATION - 1
            val endDate = DATE.plusSeconds(durationLessThanMinimum)
            val params: ValidateTenderPeriodParams = getParams(date = DATE, endDate = endDate)
            whenever(rulesService.getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType))
                .thenReturn(Duration.ofSeconds(MINIMUM_DURATION).asSuccess())
            val actual = periodService.validateTenderPeriod(params).error

            val expectedErrorCode = "VR.COM-1.17.2"
            val expectedErrorDescription = "Actual tender period duration is less than '${Duration.ofSeconds(MINIMUM_DURATION).toDays()}' days."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun tenderPeriodEndDatePrecedesDate_fail() {
            val endDate = DATE.minusSeconds(MINIMUM_DURATION)
            val params: ValidateTenderPeriodParams = getParams(date = DATE, endDate = endDate)
            whenever(rulesService.getTenderPeriodMinimumDuration(params.country, params.pmd, params.operationType))
                .thenReturn(Duration.ofSeconds(MINIMUM_DURATION).asSuccess())
            val actual = periodService.validateTenderPeriod(params).error

            val expectedErrorCode = "VR.COM-1.17.2"
            val expectedErrorDescription = "Actual tender period duration is less than '${Duration.ofSeconds(MINIMUM_DURATION).toDays()}' days."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        private fun getParams(date: LocalDateTime, endDate: LocalDateTime) = ValidateTenderPeriodParams.tryCreate(
            cpid = CPID.toString(),
            ocid = OCID.toString(),
            operationType = OperationType.START_SECOND_STAGE.key,
            pmd = ProcurementMethod.GPA.name,
            country = COUNTRY,
            date = date.format(),
            tender = ValidateTenderPeriodParams.Tender(
                tenderPeriod = ValidateTenderPeriodParams.Tender.TenderPeriod.tryCreate(
                    endDate = endDate.format()
                ).get
            )
        ).get
    }
}