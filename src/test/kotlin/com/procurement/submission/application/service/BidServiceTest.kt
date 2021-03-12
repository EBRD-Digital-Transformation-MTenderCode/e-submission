package com.procurement.submission.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.params.CheckAccessToBidParams
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class BidServiceTest {

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896")!!
        private val BID_ID = BidId.fromString("01560099-db9f-45e1-a53e-3b7fe43fd9d4")
        private val TOKEN = UUID.randomUUID()
        private val OWNER = UUID.randomUUID()
    }

    private lateinit var generationService: GenerationService
    private lateinit var rulesService: RulesService
    private lateinit var periodService: PeriodService
    private lateinit var bidRepository: BidRepository
    private lateinit var invitationRepository: InvitationRepository
    private lateinit var transform: Transform
    private lateinit var bidService: BidService

    @BeforeEach
    fun init() {
        generationService = mock()
        rulesService = mock()
        periodService = mock()
        bidRepository = mock()
        invitationRepository = mock()
        transform = mock()
        bidService = BidService(
            generationService,
            rulesService,
            periodService,
            bidRepository,
            invitationRepository,
            transform
        )
    }

    @Test
    fun checkAccessToBid_success() {
        val params = generateParams()
        whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(generateRecord().asSuccess())
        val result = bidService.checkAccessToBid(params = params)

        assertTrue(result is Validated.Ok)
    }

    @Test
    fun checkAccessToBid_bidNotFound_fail() {
        val params = generateParams()
        whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(null.asSuccess())
        val actual = bidService.checkAccessToBid(params = params) as Validated.Error

        val expectedErrorCode = "VR.COM-13.13.1"
        val expectedErrorMessage = "Bid '$BID_ID' not found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)
    }

    @Test
    fun checkAccessToBid_tokenMismatch_fail() {
        val params = generateParams()
        whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(
            generateRecord(token = UUID.randomUUID()).asSuccess()
        )
        val actual = bidService.checkAccessToBid(params = params) as Validated.Error

        val expectedErrorCode = "VR.COM-13.13.2"
        val expectedErrorMessage = "Received token does not match stored one."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)
    }

    @Test
    fun ownerMismatch_fail() {
        val params = generateParams()
        whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(
            generateRecord(owner = UUID.randomUUID()).asSuccess()
        )
        val actual = bidService.checkAccessToBid(params = params) as Validated.Error

        val expectedErrorCode = "VR.COM-13.13.3"
        val expectedErrorMessage = "Received owner does not match stored one."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)
    }

    private fun generateRecord(owner: Owner = OWNER, token: Token = TOKEN) = BidEntity.Record(
        cpid = CPID,
        ocid = OCID,
        bidId = BID_ID,
        owner = owner,
        token = token,
        status = Status.DISQUALIFIED,
        createdDate = LocalDateTime.now(),
        pendingDate = LocalDateTime.now(),
        jsonData = ""
    )

    private fun generateParams() = CheckAccessToBidParams(
        cpid = CPID,
        ocid = OCID,
        bids = CheckAccessToBidParams.Bids(details = listOf(CheckAccessToBidParams.Bids.Detail(BID_ID))),
        token = TOKEN,
        owner = OWNER
    )
}