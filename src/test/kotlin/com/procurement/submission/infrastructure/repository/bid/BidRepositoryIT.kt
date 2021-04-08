package com.procurement.submission.infrastructure.repository.bid

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
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.item.ItemId
import com.procurement.submission.failure
import com.procurement.submission.get
import com.procurement.submission.infrastructure.bind.date.JsonDateDeserializer
import com.procurement.submission.infrastructure.bind.date.JsonDateSerializer
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
import com.procurement.submission.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.model.dto.ocds.Amount
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Item
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.ocds.Requirement
import com.procurement.submission.model.dto.ocds.RequirementResponse
import com.procurement.submission.model.dto.ocds.Value
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
        private val BID_ID = BidId.fromString("01560099-db9f-45e1-a53e-3b7fe43fd9d4")
        private val DATE = JsonDateDeserializer.deserialize(JsonDateSerializer.serialize(LocalDateTime.now()))

        private fun stubBid() =
            Bid(
                id = BID_ID.toString(),
                status = BidStatus.PENDING,
                statusDetails = BidStatusDetails.WITHDRAWN,
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
                        relatedTenderer = null,
                        evidences = emptyList(),
                        period = Period(
                            startDate = DATE,
                            endDate = DATE
                        ),
                        title = null,
                        description = null
                    )
                ),
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
                            id = "id",
                            name = "name",
                            value = Value(
                                amount = BigDecimal.ONE.setScale(Amount.AVAILABLE_SCALE),
                                currency = "value.currency"
                            )

                        )

                    )
                )
            )

        private fun stubBidEntity() =
            BidEntity.New(
                cpid = CPID,
                ocid = OCID,
                pendingDate = null,
                createdDate = DATE,
                owner = Owner.randomUUID(),
                token = Token.randomUUID(),
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
    fun findByCpid_success() {
        val expectedBid = stubBidEntity()
        insertBid(expectedBid)

        val actualBids = bidRepository.findBy(cpid = CPID).get()

        assertTrue(actualBids.size == 1)
        val actualBid = actualBids[0]
        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun findByCpid_notFound_success() {
        val actualBids = bidRepository.findBy(cpid = CPID).get()

        assertTrue(actualBids.isEmpty())
    }

    @Test
    fun findByCpidAndOcid_success() {
        val expectedBid = stubBidEntity()
        insertBid(expectedBid)

        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actualBids.size == 1)
        val actualBid = actualBids[0]
        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun findByCpidAndOcid_notFound_success() {
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actualBids.isEmpty())
    }

    @Test
    fun findByCpidAndOcid_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = bidRepository.findBy(cpid = CPID, ocid = OCID).failure()

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun findByCpidAndOcidAndId_success() {
        val expectedBid = stubBidEntity()
        insertBid(expectedBid)

        val actualBid = bidRepository.findBy(cpid = CPID, ocid = OCID, id = BID_ID).get()

        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid!!.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun findByCpidAndOcidAndId_notFound_success() {
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID, id = BID_ID).get()

        assertNull(actualBids)
    }

    @Test
    fun findByCpidAndOcidAndId_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = bidRepository.findBy(cpid = CPID, ocid = OCID, id = BID_ID).failure()

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun save_success() {
        val expectedBid = stubBidEntity()
        bidRepository.save(expectedBid)
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actualBids.size == 1)
        val actualBid = actualBids[0]
        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun save_executeException_fail() {
        val expectedBid = stubBidEntity()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val actual = bidRepository.save(expectedBid).error

        assertTrue(actual is Fail.Incident.Database.Interaction)
    }

    @Test
    fun saveNewAll_success() {
        val expectedBid = stubBidEntity()
        val expectedBids = listOf(expectedBid)
        bidRepository.save(expectedBids)
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actualBids.size == 1)
        val actualBid = actualBids[0]
        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun saveNewAll_executeException_fail() {
        val bid = stubBidEntity()
        val expectedBids = listOf(bid)

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val expected = bidRepository.save(expectedBids).error

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun findByCpidAndOcidAndIds_success() {
        val expectedBid = stubBidEntity()
        insertBid(expectedBid)

        val actualBid = bidRepository.findBy(cpid = CPID, ocid = OCID, ids = listOf(BID_ID)).get().first()

        assertNotNull(actualBid)
        assertEquals(expectedBid.cpid, actualBid.cpid)
        assertEquals(expectedBid.ocid, actualBid.ocid)
        assertEquals(BID_ID, actualBid.bidId)
        assertEquals(expectedBid.token, actualBid.token)
        assertEquals(expectedBid.owner, actualBid.owner)
        assertEquals(BidStatus.PENDING, actualBid.status)
        assertEquals(expectedBid.createdDate, actualBid.createdDate)
        assertEquals(expectedBid.pendingDate, actualBid.pendingDate)
    }

    @Test
    fun findByCpidAndOcidAndIds_notFound_success() {
        val actualBids = bidRepository.findBy(cpid = CPID, ocid = OCID, id = BID_ID).get()

        assertNull(actualBids)
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE ${Database.KEYSPACE} " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ${Database.KEYSPACE};")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.Bids.TABLE}
                    (
                        ${Database.Bids.CPID} TEXT,
                        ${Database.Bids.OCID} TEXT,
                        ${Database.Bids.ID} TEXT,
                        ${Database.Bids.OWNER} TEXT,
                        ${Database.Bids.TOKEN} TEXT,
                        ${Database.Bids.STATUS} TEXT,
                        ${Database.Bids.CREATED_DATE} TIMESTAMP,
                        ${Database.Bids.PENDING_DATE} TIMESTAMP,
                        ${Database.Bids.JSON_DATA} TEXT,
                        PRIMARY KEY(${Database.Bids.CPID}, ${Database.Bids.OCID}, ${Database.Bids.ID})
                    );
            """
        )
    }

    private fun insertBid(bidEntity: BidEntity.New) {
        val jsonData = transform.trySerialization(bidEntity.bid).get()
        val record = QueryBuilder.insertInto(Database.KEYSPACE, Database.Bids.TABLE)
            .value(Database.Bids.CPID, bidEntity.cpid.toString())
            .value(Database.Bids.OCID, bidEntity.ocid.toString())
            .value(Database.Bids.OWNER, bidEntity.owner.toString())
            .value(Database.Bids.ID, bidEntity.bid.id)
            .value(Database.Bids.TOKEN, bidEntity.token.toString())
            .value(Database.Bids.STATUS, bidEntity.bid.status.toString())
            .value(Database.Bids.CREATED_DATE, bidEntity.createdDate.toCassandraTimestamp())
            .value(Database.Bids.PENDING_DATE, bidEntity.pendingDate?.toCassandraTimestamp())
            .value(Database.Bids.JSON_DATA, jsonData)
        session.execute(record)
    }
}
