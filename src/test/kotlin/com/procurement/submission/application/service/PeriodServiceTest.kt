package com.procurement.submission.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodContext
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodResult
import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.application.params.SetTenderPeriodParams
import com.procurement.submission.application.params.ValidateTenderPeriodParams
import com.procurement.submission.domain.extension.format
import com.procurement.submission.domain.extension.toDate
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.Stage
import com.procurement.submission.infrastructure.dao.PeriodDao
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodResult
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.ValidationResult
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.entity.PeriodEntity
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
        private val STAGE = Stage.AC
        private val PMD = ProcurementMethod.CF

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

            val expectedErrorCode = "VR.COM-13.4.2"
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

            val expectedErrorCode = "VR.COM-13.4.2"
            val expectedErrorDescription = "Actual tender period duration is less than '${Duration.ofSeconds(MINIMUM_DURATION).toDays()}' days."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        private fun getParams(date: LocalDateTime, endDate: LocalDateTime) = ValidateTenderPeriodParams.tryCreate(
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

    @Nested
    inner class SetTenderPeriod {
        @Test
        fun success() {
            val params: SetTenderPeriodParams = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.toDate(),
                endDate = params.tender.tenderPeriod.endDate.toDate()
            )
            whenever(periodDao.trySave(entity)).thenReturn(MaybeFail.none())
            val actual = periodService.setTenderPeriod(params).get

            val expected = SetTenderPeriodResult(
                tender = SetTenderPeriodResult.Tender(
                    SetTenderPeriodResult.Tender.TenderPeriod(
                        startDate = params.date,
                        endDate = params.tender.tenderPeriod.endDate
                    )
                )
            )
            assertEquals(expected, actual)
        }

        private fun getParams() = SetTenderPeriodParams.tryCreate(
            cpid = CPID.toString(),
            ocid = OCID.toString(),
            date = DATE.format(),
            tender = SetTenderPeriodParams.Tender(
                tenderPeriod = SetTenderPeriodParams.Tender.TenderPeriod.tryCreate(
                    endDate = DATE.plusDays(1).format()
                ).get
            )
        ).get
    }

    @Nested
    inner class CheckPeriod {
        @Test
        fun success() {
            val params = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.minusDays(1).toDate(),
                endDate = params.date.plusDays(1).toDate()
            )
            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(entity.asSuccess())
            val actual = periodService.checkPeriod(params)

            assertTrue(actual is ValidationResult.Ok)
        }

        @Test
        fun periodNotFound_fail() {
            val params = getParams()

            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(null.asSuccess())
            val actual = periodService.checkPeriod(params).error

            val expectedErrorCode = "VR.COM-13.6.1"
            val expectedErrorDescription = "Tender period by cpid '${params.cpid}' and ocid '${params.ocid}' not found."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun datePrecedesStartDate_fail() {
            val params = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.plusSeconds(1).toDate(),
                endDate = params.date.plusDays(1).toDate()
            )

            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(entity.asSuccess())
            val actual = periodService.checkPeriod(params).error

            val expectedErrorCode = "VR.COM-13.6.2"
            val expectedErrorDescription = "Received date must be after stored start date."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun dateEqualsStartDate_fail() {
            val params = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.toDate(),
                endDate = params.date.plusDays(1).toDate()
            )

            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(entity.asSuccess())
            val actual = periodService.checkPeriod(params).error

            val expectedErrorCode = "VR.COM-13.6.2"
            val expectedErrorDescription = "Received date must be after stored start date."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun dateIsAfterEndDate_fail() {
            val params = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.minusSeconds(1).toDate(),
                endDate = params.date.minusSeconds(1).toDate()
            )

            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(entity.asSuccess())
            val actual = periodService.checkPeriod(params).error

            val expectedErrorCode = "VR.COM-13.6.3"
            val expectedErrorDescription = "Received date must precede stored end date."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun dateEqualsEndDate_fail() {
            val params = getParams()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = params.date.minusSeconds(1).toDate(),
                endDate = params.date.toDate()
            )

            whenever(periodDao.tryGetBy(cpid = params.cpid, stage = params.ocid.stage)).thenReturn(entity.asSuccess())
            val actual = periodService.checkPeriod(params).error

            val expectedErrorCode = "VR.COM-13.6.3"
            val expectedErrorDescription = "Received date must precede stored end date."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        private fun getParams() = CheckPeriodParams.tryCreate(
            cpid = CPID.toString(),
            ocid = OCID.toString(),
            date = DATE.format()
        ).get
    }

    @Nested
    inner class ExtendTenderPeriod {
        @Test
        fun success() {
            val context = getContext()

            val entity = PeriodEntity(
                cpId = CPID.toString(),
                stage = OCID.stage.toString(),
                startDate = DATE.plusDays(1).toDate(),
                endDate = DATE.plusDays(2).toDate()
            )
            whenever(periodDao.getByCpIdAndStage(cpId = context.cpid, stage = context.stage)).thenReturn(entity)
            whenever(rulesService.getExtensionAfterUnsuspended(country = context.country, pmd = context.pmd)).thenReturn(
                Duration.ofSeconds(Duration.ofDays(10).seconds))

            val actual = periodService.extendTenderPeriod(context)
            val expected = ExtendTenderPeriodResult(
                ExtendTenderPeriodResult.TenderPeriod(
                    startDate = LocalDateTime.parse("2020-02-11T08:49:55Z", FORMATTER),
                    endDate = LocalDateTime.parse("2020-02-20T08:49:55Z", FORMATTER)
                )
            )

            assertEquals(expected, actual)
        }


        private fun getContext() = ExtendTenderPeriodContext(
            cpid = CPID.toString(),
            stage = STAGE.toString(),
            startDate = DATE,
            pmd = PMD,
            country = COUNTRY
        )
    }
}