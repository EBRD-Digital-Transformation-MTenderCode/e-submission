package com.procurement.submission.domain.rule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails
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
                    from(BidStatus.VALID, BidStatusDetails.ARCHIVED),
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                )
            )

            assertTrue(validStates.contains(BidStatus.VALID, BidStatusDetails.ARCHIVED))
        }

        @Test
        fun `doesn't matches by FULL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.VALID, BidStatusDetails.ARCHIVED),
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                )
            )

            assertFalse(validStates.contains(BidStatus.VALID, BidStatusDetails.INVITED))
        }

        @Test
        fun `matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.VALID, null),
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                )
            )

            assertTrue(validStates.contains(BidStatus.VALID, null))
        }

        @Test
        fun `doesn't matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.VALID, null),
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                )
            )

            assertFalse(validStates.contains(BidStatus.VALID, BidStatusDetails.WITHDRAWN))
        }

        @Test
        fun `matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(BidStatus.VALID), null),
                )
            )

            assertTrue(validStates.contains(BidStatus.VALID, BidStatusDetails.ARCHIVED))
        }

        @Test
        fun `doesn't matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(BidStatus.VALID), null),
                )
            )

            assertFalse(validStates.contains(BidStatus.INVITED, BidStatusDetails.ARCHIVED))
        }

        @Test
        fun `matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(BidStatus.VALID), null),
                )
            )

            assertTrue(validStates.contains(BidStatus.VALID, null))
        }

        @Test
        fun `doesn't matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    from(BidStatus.PENDING, BidStatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(BidStatus.VALID), null),
                )
            )
            assertFalse(validStates.contains(BidStatus.DISQUALIFIED, null))
        }

        private fun from(status: BidStatus, statusDetails: BidStatusDetails?): ValidBidStatesRule.State =
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

            assertTrue(rule.contains(BidStatus.INVITED, BidStatusDetails.PENDING))
        }

        @Test
        fun `has FULL state when statusDetails specified as null`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": { "value": null } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(BidStatus.INVITED, null))
        }

        @Test
        fun `has FULL state when statusDetails specified without value`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": {  } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(BidStatus.INVITED, null))
        }

        @Test
        fun `has PARTIAL state when statusDetails not specified`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" } } ] """
            val rule = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java)

            assertTrue(rule.contains(BidStatus.INVITED, BidStatusDetails.WITHDRAWN))
        }

    }

}