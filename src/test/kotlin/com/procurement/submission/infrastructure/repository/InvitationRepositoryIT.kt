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
import com.procurement.submission.application.repository.InvitationRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.model.entity.InvitationEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class InvitationRepositoryIT {

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!

        private const val KEYSPACE = "ocds"
        private const val INVITATION_TABLE = "submission_invitation"
        private const val CPID_COLUMN = "cpid"
        private const val ID_COLUMN = "id"
        const val JSON_DATA_COLUMN = "json_data"

        private val DATE = JsonDateDeserializer.deserialize(JsonDateSerializer.serialize(LocalDateTime.now()))
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    @Autowired
    private lateinit var transform: Transform

    private lateinit var session: Session
    private lateinit var invitationRepository: InvitationRepository

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

        invitationRepository = InvitationRepositoryCassandra(session, transform)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy_success() {
        val expectedInvitationFirst = stubInvitation()
        val expectedInvitationSecond = stubInvitation()
        val additionalInvitation = stubInvitation()
        insertInvitation(cpid = CPID, invitation = expectedInvitationFirst)
        insertInvitation(cpid = CPID, invitation = expectedInvitationSecond)
        insertInvitation(
            cpid = Cpid.tryCreateOrNull("ocds-t1s2t3-EV-2565251033153")!!,
            invitation = additionalInvitation
        )

        val expectedInvitations = setOf(expectedInvitationFirst, expectedInvitationSecond)

        val actualInvitations = invitationRepository.findBy(cpid = CPID).get

        assertEquals(expectedInvitations, actualInvitations.toSet())
        assertTrue(actualInvitations.size == 2)
    }

    @Test
    fun findBy_InvitationsNotFound_success() {
        val actualInvitations = invitationRepository.findBy(cpid = CPID).get

        assertTrue(actualInvitations.isEmpty())
    }

    @Test
    fun findBy_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = invitationRepository.findBy(cpid = CPID).error

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun saveInvitation_success() {
        val invitation = stubInvitation()
        invitationRepository.save(cpid = CPID, invitation = invitation)

        val actualInvitation = invitationRepository.findBy(cpid = CPID).get.first()

        assertEquals(invitation, actualInvitation)
    }

    @Test
    fun saveInvitation_executeException_fail() {
        val expectedInvitation = stubInvitation()
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = invitationRepository.save(CPID, expectedInvitation).error

        assertTrue(expected is Fail.Incident.Database.Interaction)
    }

    @Test
    fun saveAllInvitations_success() {
        val invitationFirst = stubInvitation()
        val invitationSecond = stubInvitation()
        val expectedInvitations = listOf(invitationFirst, invitationSecond)
        invitationRepository.saveAll(cpid = CPID, invitations = expectedInvitations)
        val actualInvitations = invitationRepository.findBy(cpid = CPID).get

        assertEquals(expectedInvitations.toSet(), actualInvitations.toSet())
        assertTrue(actualInvitations.size == 2)
    }

    @Test
    fun saveAllInvitations_executeException_fail() {
        val invitationFirst = stubInvitation()
        val invitationSecond = stubInvitation()
        val expectedInvitations = listOf(invitationFirst, invitationSecond)
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val expected = invitationRepository.saveAll(CPID, expectedInvitations).error

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
                CREATE TABLE IF NOT EXISTS $KEYSPACE.$INVITATION_TABLE
                    (
                        $CPID_COLUMN text,
                        $ID_COLUMN text,
                        $JSON_DATA_COLUMN text,
                        primary key($CPID_COLUMN, $ID_COLUMN)
                    );
            """
        )
    }

    private fun insertInvitation(cpid: Cpid, invitation: Invitation) {
        val jsonData = transform.trySerialization(convert(invitation)).get
        val record = QueryBuilder.insertInto(KEYSPACE, INVITATION_TABLE)
            .value(CPID_COLUMN, cpid.toString())
            .value(ID_COLUMN, invitation.id.toString())
            .value(JSON_DATA_COLUMN, jsonData)
        session.execute(record)
    }

    private fun convert(invitation: Invitation) = InvitationEntity(
        id = invitation.id,
        date = invitation.date,
        status = invitation.status,
        relatedQualification = invitation.relatedQualification,
        tenderers = invitation.tenderers.map { tenderer ->
            InvitationEntity.Tenderer(
                id = tenderer.id,
                name = tenderer.name
            )
        }
    )

    private fun stubInvitation() =
        Invitation(
            id = InvitationId.generate(),
            relatedQualification = QualificationId.generate(),
            date = DATE,
            tenderers = listOf(
                Invitation.Tenderer(
                    id = "tender.id",
                    name = "tender.name"
                )
            ),
            status = InvitationStatus.ACTIVE
        )
}


