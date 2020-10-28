package com.procurement.submission.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.application.repository.BidRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.extension.toDate
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.domain.model.item.ItemId
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.model.dto.ocds.Amount
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Item
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.ocds.Requirement
import com.procurement.submission.model.dto.ocds.RequirementResponse
import com.procurement.submission.model.dto.ocds.Value
import com.procurement.submission.model.entity.BidEntityComplex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class BidRepositoryIT {

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896")!!

        private const val KEYSPACE = "ocds"
        private const val BID_TABLE = "submission_invitation"
        private const val CPID_COLUMN = "cp_id"
        private const val STAGE_COLUMN = "stage"
        private const val BID_ID_COLUMN = "bid_id"
        private const val TOKEN_COLUMN = "token_entity"
        private const val OWNER_COLUMN = "owner"
        private const val STATUS_COLUMN = "status"
        private const val CREATED_DATE_COLUMN = "created_date"
        private const val PENDING_DATE_COLUMN = "pending_date"
        private const val JSON_DATA_COLUMN = "json_data"

        private val DATE = JsonDateDeserializer.deserialize(JsonDateSerializer.serialize(LocalDateTime.now()))

        private fun stubBid() =
            Bid(
                id = "id",
                status = Status.PENDING,
                statusDetails = StatusDetails.WITHDRAWN,
                date = DATE,
                value = Money(
                    amount = BigDecimal.ONE.setScale(Amount.AVAILABLE_SCALE),
                    currency = "currency"
                ),
                requirementResponses =
                listOf(
                    RequirementResponse(
                        id = "requirementResponse.id",
                        value = RequirementRsValue.AsString("requirementResponse.value"),
                        requirement = Requirement("requirementResponse.requirement.id"),
                        period = Period(
                            startDate = DATE,
                            endDate = DATE
                        ),
                        title = null,
                        description = null
                    )
                )
                ,
                tenderers = emptyList(),
                relatedLots = listOf("relatedLots"),
                documents = listOf(
                    Document(
                        documentType = DocumentType.COMMERCIAL_OFFER,
                        id = "document.id",
                        title = "document.title",
                        description = "document.description",
                        relatedLots = listOf("relatedLots")
                    )
                ),
                items = listOf(
                    Item(
                        id = ItemId.generate(),
                        unit = Item.Unit(
                            value = Value(
                                amount = BigDecimal.ONE.setScale(Amount.AVAILABLE_SCALE),
                                currency = "value.currency"
                            )

                        )

                    )
                )
            )

        private fun stubBidEntity() =
            BidEntityComplex(
                cpid = CPID,
                stage = OCID.stage,
                pendingDate = DATE.toDate(),
                createdDate = DATE.toDate(),
                owner = Owner.randomUUID(),
                bidId = BidId.randomUUID(),
                token = Token.randomUUID(),
                status = Status.PENDING,
                bid = stubBid()
            )
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    @Autowired
    private lateinit var transform: Transform

    private lateinit var session: Session
    private lateinit var bidRepository: BidRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        bidRepository = BidRepositoryCassandra(session, transform)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy_success() {
        val expectedBid = stubBidEntity()
        insertBid(expectedBid)

        val expectedBids = listOf(expectedBid)

        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get

        assertEquals(expectedBids, actualBids)
    }

    @Test
    fun findBy_InvitationsNotFound_success() {
        val actualInvitations = bidRepository.findBy(cpid = CPID, ocid = OCID).get

        assertTrue(actualInvitations.isEmpty())
    }

    @Test
    fun findBy_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = bidRepository.findBy(cpid = CPID, ocid = OCID).error

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun saveAll_success() {
        val bid = stubBidEntity()
        val expectedBids = listOf(bid)
        bidRepository.saveAll(expectedBids)
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get

        assertEquals(expectedBids, actualBids)
    }

    @Test
    fun saveAll_executeException_fail() {
        val bid = stubBidEntity()
        val expectedBids = listOf(bid)

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val expected = bidRepository.saveAll(expectedBids).error

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE $KEYSPACE " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE $KEYSPACE;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS $KEYSPACE.$BID_TABLE
                    (
                        $CPID_COLUMN text,
                        $STAGE_COLUMN text,
                        $OWNER_COLUMN text,
                        $BID_ID_COLUMN uuid,
                        $TOKEN_COLUMN uuid,
                        $STATUS_COLUMN text,
                        $CREATED_DATE_COLUMN timestamp,
                        $PENDING_DATE_COLUMN timestamp,
                        $JSON_DATA_COLUMN text,
                        primary key($CPID_COLUMN, $STAGE_COLUMN, $BID_ID_COLUMN)
                    );
            """
        )
    }

    private fun insertBid(bidEntity: BidEntityComplex) {
        val jsonData = transform.trySerialization(bidEntity.bid).get
        val record = QueryBuilder.insertInto(KEYSPACE, BID_TABLE)
            .value(CPID_COLUMN, bidEntity.cpid.toString())
            .value(STAGE_COLUMN, bidEntity.stage.toString())
            .value(OWNER_COLUMN, bidEntity.owner.toString())
            .value(BID_ID_COLUMN, bidEntity.bidId)
            .value(TOKEN_COLUMN, bidEntity.token)
            .value(STATUS_COLUMN, bidEntity.status.toString())
            .value(CREATED_DATE_COLUMN, bidEntity.createdDate)
            .value(PENDING_DATE_COLUMN, bidEntity.pendingDate)
            .value(JSON_DATA_COLUMN, jsonData)
        session.execute(record)
    }


}


