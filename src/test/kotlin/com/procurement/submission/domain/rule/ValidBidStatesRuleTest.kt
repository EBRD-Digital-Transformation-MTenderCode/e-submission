package com.procurement.submission.domain.rule

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.infrastructure.bind.configuration
import com.procurement.submission.infrastructure.configuration.ObjectMapperConfig
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
                    ValidBidStatesRule.State.from(Status.VALID, StatusDetails.ARCHIVED),
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, StatusDetails.ARCHIVED)

            assertTrue(currectState in validStates)
        }

        @Test
        fun `doesn't matches by FULL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.VALID, StatusDetails.ARCHIVED),
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, StatusDetails.INVITED)

            assertFalse(currectState in validStates)
        }

        @Test
        fun `matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.VALID, null),
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, null)

            assertTrue(currectState in validStates)
        }

        @Test
        fun `doesn't matches by PARTIAL state with FULL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.VALID, null),
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, StatusDetails.WITHDRAWN)

            assertFalse(currectState in validStates)
        }

        @Test
        fun `matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, StatusDetails.ARCHIVED)

            assertTrue(currectState in validStates)
        }

        @Test
        fun `doesn't matches by FULL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.INVITED, StatusDetails.ARCHIVED)

            assertFalse(currectState in validStates)
        }

        @Test
        fun `matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.VALID, null)

            assertTrue(currectState in validStates)
        }

        @Test
        fun `doesn't matches by PERTIAL state with PARTIAL valid states`() {
            val validStates = ValidBidStatesRule(
                listOf(
                    ValidBidStatesRule.State.from(Status.PENDING, StatusDetails.WITHDRAWN),
                    ValidBidStatesRule.State(ValidBidStatesRule.State.ValidStatus(Status.VALID), null),
                )
            )
            val currectState = ValidBidStatesRule.State.from(Status.DISQUALIFIED, null)

            assertFalse(currectState in validStates)
        }

    }

    @Nested
    inner class Mapping {

        @Test
        fun `has FULL state when it explicit specified`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": { "value": "pending" } } ] """
            val validState = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java).first()

            assertNotNull(validState.statusDetails)
            assertNotNull(validState.statusDetails?.value)
        }

        @Test
        fun `has FULL state when statusDetails specified as null`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": { "value": null } } ] """
            val validState = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java).first()

            assertNotNull(validState.statusDetails)
            assertNull(validState.statusDetails?.value)
        }

        @Test
        fun `has FULL state when statusDetails specified without value`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" }, "statusDetails": {  } } ] """
            val validState = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java).first()

            assertNotNull(validState.statusDetails)
            assertNull(validState.statusDetails?.value)
        }

        @Test
        fun `has PARTIAL state when statusDetails not specified`() {
            val jsonWithStates = """ [ { "status": {  "value": "invited" } } ] """
            val validState = mapper.readValue(jsonWithStates, ValidBidStatesRule::class.java).first()

            assertNull(validState.statusDetails)
            assertNull(validState.statusDetails?.value)
        }


    }

}