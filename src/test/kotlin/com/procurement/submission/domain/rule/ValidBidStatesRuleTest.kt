package com.procurement.submission.domain.rule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.infrastructure.bind.configuration
import com.procurement.submission.infrastructure.configuration.ObjectMapperConfig
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ObjectMapperConfig::class])
internal class ValidBidStatesRuleTest {

    private val mapper: ObjectMapper = jacksonObjectMapper().apply { configuration() }

    @Nested
    inner class Contains {

        @Test
        fun `matches by FULL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.VALID, StatusDetails.ARCHIVED),
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )

            assertTrue(validStates.contains(Status.VALID, StatusDetails.ARCHIVED))
        }

        @Test
        fun `doesn't matches by FULL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.VALID, StatusDetails.ARCHIVED),
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )

            assertFalse(validStates.contains(Status.VALID, StatusDetails.INVITED))
        }

        @Test
        fun `matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.VALID, null),
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )

            assertTrue(validStates.contains(Status.VALID, null))
        }

        @Test
        fun `doesn't matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.VALID, null),
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )

            assertFalse(validStates.contains(Status.VALID, StatusDetails.WITHDRAWN))
        }

        @Test
        fun `matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )

            assertTrue(validStates.contains(Status.VALID, StatusDetails.ARCHIVED))
        }

        @Test
        fun `doesn't matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )

            assertFalse(validStates.contains(Status.INVITED, StatusDetails.ARCHIVED))
        }

        @Test
        fun `matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )

            assertTrue(validStates.contains(Status.VALID, null))
        }

        @Test
        fun `doesn't matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )
            assertFalse(validStates.contains(Status.DISQUALIFIED, null))
        }

        private fun from(status: Status, statusDetails: StatusDetails?): ValidBidStatesRule.State =
            ValidBidStatesRule.State(
                ValidBidStatesRule.State.ValidStatus(status),
                ValidBidStatesRule.State.ValidStatusDetails(statusDetails)
            )

    }

    @Nested
    inner class Mapping {

        @Test
        fun `has FULL state when it explicit specified`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": { "value": "pending" } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(Status.INVITED, StatusDetails.PENDING))
        }

        @Test
        fun `has FULL state when statusDetails specified as null`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": { "value": null } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(Status.INVITED, null))
        }

        @Test
        fun `has FULL state when statusDetails specified without value`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": {  } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(Status.INVITED, null))
        }

        @Test
        fun `has PARTIAL state when statusDetails not specified`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(Status.INVITED, StatusDetails.WITHDRAWN))
        }

    }

}