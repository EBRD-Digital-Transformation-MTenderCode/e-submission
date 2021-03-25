import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.params.CheckAccessToBidParams
import com.procurement.submission.application.params.CheckBidStateParams
import com.procurement.submission.application.params.SetStateForBidsParams
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.GenerationService
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.RulesService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.domain.rule.BidStateForSettingRule
import com.procurement.submission.domain.rule.ValidBidStatesRule
import com.procurement.submission.domain.rule.from
import com.procurement.submission.get
import com.procurement.submission.infrastructure.handler.v2.model.response.SetStateForBidsResult
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
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
        private val OPERATION_TYPE = OperationType.WITHDRAW_BID
        private val PMD = ProcurementMethod.TEST_SV
        private val STATUS = Status.INVITED
        private val STATUS_DETAILS = StatusDetails.ARCHIVED
        private val COUNTRY = "country"
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

    @Nested
    inner class CheckAccessToBid {

        @Test
        fun success() {
            val params = generateParams()
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(generateRecord().asSuccess())
            val result = bidService.checkAccessToBid(params = params)

            assertTrue(result is Validated.Ok)
        }

        @Test
        fun bidNotFound_fail() {
            val params = generateParams()
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(null.asSuccess())
            val actual = bidService.checkAccessToBid(params = params) as Validated.Error

            val expectedErrorCode = "VR.COM-13.13.1"
            val expectedErrorMessage = "Bid '$BID_ID' not found."

            assertEquals(expectedErrorCode, actual.reason.code)
            assertEquals(expectedErrorMessage, actual.reason.description)
        }

        @Test
        fun tokenMismatch_fail() {
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

    @Nested
    inner class CheckBidState {

        @Test
        fun statusAndDetailsMatches_success() {
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(getRecord().asSuccess())
            whenever(transform.tryDeserialization(any(), any<Class<*>>())).thenReturn(getBid().asSuccess())
            val allowedStates = listOf(ValidBidStatesRule.State.from(STATUS, STATUS_DETAILS))
            whenever(
                rulesService.getValidStates(
                    COUNTRY,
                    PMD,
                    OPERATION_TYPE
                )
            ).thenReturn(ValidBidStatesRule(allowedStates).asSuccess())
            val actual = bidService.checkBidState(getParams())

            assertTrue(actual is Validated.Ok)
        }

        @Test
        fun statusMatchesDetailsNull_success() {
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(getRecord().asSuccess())
            whenever(transform.tryDeserialization(any(), any<Class<*>>())).thenReturn(getBid().asSuccess())
            val allowedStates = listOf(ValidBidStatesRule.State(
                ValidBidStatesRule.State.ValidStatus(STATUS),
                null
            ))
            whenever(
                rulesService.getValidStates(
                    COUNTRY,
                    PMD,
                    OPERATION_TYPE
                )
            ).thenReturn(ValidBidStatesRule(allowedStates).asSuccess())
            val actual = bidService.checkBidState(getParams())

            assertTrue(actual is Validated.Ok)
        }

        @Test
        fun statusAndDetailsMisMatches_fail() {
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(getRecord().asSuccess())
            whenever(transform.tryDeserialization(any(), any<Class<*>>())).thenReturn(getBid().asSuccess())
            val allowedStates = listOf(ValidBidStatesRule.State.from(STATUS, StatusDetails.INVITED))
            whenever(
                rulesService.getValidStates(
                    COUNTRY,
                    PMD,
                    OPERATION_TYPE
                )
            ).thenReturn(ValidBidStatesRule(allowedStates).asSuccess())
            val actual = bidService.checkBidState(getParams()) as Validated.Error

            val errorCode = "VR.COM-13.14.2"
            val errorMessage = "Bid's '$BID_ID' state is invalid."
            assertEquals(errorCode, actual.reason.code)
            assertEquals(errorMessage, actual.reason.description)
        }

        @Test
        fun bidNotFound_fail() {
            whenever(bidRepository.findBy(CPID, OCID, BID_ID)).thenReturn(null.asSuccess())
            val actual = bidService.checkBidState(getParams()) as Validated.Error

            val errorCode = "VR.COM-13.14.1"
            val errorMessage = "Bid '$BID_ID' not found."
            assertEquals(errorCode, actual.reason.code)
            assertEquals(errorMessage, actual.reason.description)
        }

        private fun getParams() = CheckBidStateParams(
            cpid = CPID,
            ocid = OCID,
            operationType = OPERATION_TYPE,
            country = COUNTRY,
            pmd = PMD,
            bids = CheckBidStateParams.Bids(listOf(CheckBidStateParams.Bids.Detail(BID_ID)))
        )

        private fun getRecord() = BidEntity.Record(
            cpid = CPID,
            ocid = OCID,
            bidId = BID_ID,
            pendingDate = LocalDateTime.now(),
            token = TOKEN,
            owner = OWNER,
            createdDate = LocalDateTime.now(),
            status = STATUS,
            jsonData = ""
        )

        private fun getBid() = Bid(
            id = BID_ID.toString(),
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            value = mock(),
            requirementResponses = null,
            relatedLots = emptyList(),
            items = null,
            documents = null,
            date = LocalDateTime.now(),
            tenderers = emptyList()
        )
    }

    @Nested
    inner class SetStateForBids{
        @Test
        fun success() {
            whenever(bidRepository.findBy(CPID, OCID, listOf(BID_ID))).thenReturn(listOf(getRecord()).asSuccess())
            whenever(transform.tryDeserialization(any(), any<Class<*>>())).thenReturn(getBid().asSuccess())
            val newState = BidStateForSettingRule(Status.EMPTY, StatusDetails.DISQUALIFIED)
            whenever(rulesService.getStateForSetting(COUNTRY, PMD, OPERATION_TYPE)).thenReturn(newState.asSuccess())
            whenever(bidRepository.save(any<Collection<BidEntity>>())).thenReturn(MaybeFail.none())

            val actual = bidService.setStateForBids(getParams())
            val expected = SetStateForBidsResult(SetStateForBidsResult.Bids(listOf(
                SetStateForBidsResult.Bids.Detail(
                id = BID_ID.toString(),
                status = newState.status,
                statusDetails = newState.statusDetails!!
            ))))

            assertEquals(expected,  actual.get())
        }

        @Test
        fun statusDetailsIsNull_success() {
            whenever(bidRepository.findBy(CPID, OCID, listOf(BID_ID))).thenReturn(listOf(getRecord()).asSuccess())
            val bid = getBid()
            whenever(transform.tryDeserialization(any(), any<Class<*>>())).thenReturn(bid.asSuccess())
            val newState = BidStateForSettingRule(Status.EMPTY, null)
            whenever(rulesService.getStateForSetting(COUNTRY, PMD, OPERATION_TYPE)).thenReturn(newState.asSuccess())
            whenever(bidRepository.save(any<Collection<BidEntity>>())).thenReturn(MaybeFail.none())
            val actual = bidService.setStateForBids(getParams())
            val expected = SetStateForBidsResult(SetStateForBidsResult.Bids(listOf(
                SetStateForBidsResult.Bids.Detail(
                    id = BID_ID.toString(),
                    status = newState.status,
                    statusDetails = bid.statusDetails
                ))))

            assertEquals(expected,  actual.get())
        }

        @Test
        fun oneBidIsMissing_fail() {
            val unknownBidId = BidId.randomUUID()
            val params = SetStateForBidsParams(
                cpid = CPID,
                ocid = OCID,
                operationType = OPERATION_TYPE,
                country = COUNTRY,
                pmd = PMD,
                bids = SetStateForBidsParams.Bids(listOf(SetStateForBidsParams.Bids.Detail(BID_ID), SetStateForBidsParams.Bids.Detail(unknownBidId)))
            )
            whenever(bidRepository.findBy(CPID, OCID, listOf(BID_ID, unknownBidId))).thenReturn(listOf(getRecord()).asSuccess())

            val actual = bidService.setStateForBids(params) as Result.Failure

            val expectedErrorCode = "VR.COM-13.15.1"
            val expectedMessage = "Bid(s) '$unknownBidId' not found."

            assertEquals(expectedErrorCode,  actual.reason.code)
            assertEquals(expectedMessage,  actual.reason.description)

        }

        private fun getParams() = SetStateForBidsParams(
            cpid = CPID,
            ocid = OCID,
            operationType = OPERATION_TYPE,
            country = COUNTRY,
            pmd = PMD,
            bids = SetStateForBidsParams.Bids(listOf(SetStateForBidsParams.Bids.Detail(BID_ID)))
        )

        private fun getRecord() = BidEntity.Record(
            cpid = CPID,
            ocid = OCID,
            bidId = BID_ID,
            pendingDate = LocalDateTime.now(),
            token = TOKEN,
            owner = OWNER,
            createdDate = LocalDateTime.now(),
            status = STATUS,
            jsonData = ""
        )

        private fun getBid() = Bid(
            id = BID_ID.toString(),
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            value = mock(),
            requirementResponses = null,
            relatedLots = emptyList(),
            items = null,
            documents = null,
            date = LocalDateTime.now(),
            tenderers = emptyList()
        )
    }
}