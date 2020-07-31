package com.procurement.submission.application.commands

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.service.checkTenderersInvitations
import com.procurement.submission.application.service.checkTenderersInvitedToTender
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.ProcurementMethod
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertDoesNotThrow

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBidTest {

    companion object {
        private const val CPID = "ocds-b3wdp1-MD-1580458690892"
    }

    @Nested
    inner class CheckInvitationsForTenderer {

        @Test
        @DisplayName("Not GPA procedure")
        fun invalidPmd() {
            val invalidPmd = ProcurementMethod.FA
            assertDoesNotThrow {
                checkTenderersInvitations(CPID, invalidPmd, emptyList(), ::getInvitedTenderers)
            }
        }

        @Test
        @DisplayName("Invalid CPID")
        fun invalidCpid() {
            val invalidCpid = "N/A"
            val error = assertThrows<ErrorException> {
                checkTenderersInvitations(invalidCpid, ProcurementMethod.GPA, emptyList(), ::getInvitedTenderers)
            }
            assertEquals(ErrorType.INVALID_FORMAT_OF_ATTRIBUTE.code, error.code)
        }

        @Test
        @DisplayName("All bid's tenderers have active invitation")
        fun allTenderersHaveInvitations() {
            assertDoesNotThrow {
                checkTenderersInvitedToTender(bidTenderers, fullInvitations)
            }
        }

        @Test
        @DisplayName("Not all bid.s tenderers have acrive invitations")
        fun notAllTenderersHaveActiveInvitations() {
            val error = assertThrows<ErrorException> {
                checkTenderersInvitedToTender(bidTenderers, partitialInvitations)
            }
            assertEquals(ErrorType.RELATION_NOT_FOUND.code, error.code)
        }


        private val bidTenderers = listOf(
            "MD-IDNO-271-78-0001",
            "MD-IDNO-271-78-0002"
        )

        private val fullInvitations: Set<String> = setOf(
            "MD-IDNO-271-78-0001",
            "MD-IDNO-271-78-0002"
        )

        private val partitialInvitations: Set<String> = setOf("MD-IDNO-271-78-0001")

        private fun getInvitedTenderers(cpid: Cpid): Set<String> = emptySet()

    }

}