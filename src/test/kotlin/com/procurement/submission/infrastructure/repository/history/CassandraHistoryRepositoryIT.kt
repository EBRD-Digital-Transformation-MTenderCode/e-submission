package com.procurement.submission.infrastructure.repository.history

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.nhaarman.mockito_kotlin.spy
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.extension.format
import com.procurement.submission.domain.extension.parseLocalDateTime
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.model.dto.bpe.CommandType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraHistoryRepositoryIT {

    companion object {
        private val COMMAND_ID: CommandId = CommandId(UUID.randomUUID().toString())
        private val COMMAND_NAME: Action = CommandType.BID_WITHDRAWN
        private val COMMAND_DATE = LocalDateTime.now().format().parseLocalDateTime()
        private const val JSON_DATA: String = """{"tender": {"title" : "Tender-Title"}}"""

        private val HISTORY_ENTITY = HistoryEntity(
            commandId = COMMAND_ID,
            action = COMMAND_NAME,
            date = COMMAND_DATE,
            data = JSON_DATA
        )
    }

    @Autowired
    private lateinit var container: CassandraTestContainer
    private lateinit var session: Session
    private lateinit var repository: HistoryRepository

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

        repository = CassandraHistoryRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun saveHistory() {
        val result = repository.saveHistory(HISTORY_ENTITY)

        assertTrue(result.isSuccess)
        result.forEach {
            assertEquals(HISTORY_ENTITY.commandId, it.commandId)
            assertEquals(HISTORY_ENTITY.action, it.action)
            assertEquals(HISTORY_ENTITY.date, it.date)
            assertEquals(HISTORY_ENTITY.data, it.data)
        }
    }

    @Test
    fun getHistory() {

        val savedResult = repository.saveHistory(HISTORY_ENTITY)

        assertTrue(savedResult.isSuccess)
        savedResult.forEach {
            assertEquals(HISTORY_ENTITY.commandId, it.commandId)
            assertEquals(HISTORY_ENTITY.action, it.action)
            assertEquals(HISTORY_ENTITY.date, it.date)
            assertEquals(HISTORY_ENTITY.data, it.data)
        }

        val loadedResult = repository.getHistory(commandId = HISTORY_ENTITY.commandId)
        assertTrue(loadedResult.isSuccess)
        loadedResult.forEach {
            assertNotNull(it)
            assertEquals(HISTORY_ENTITY.data, it)
        }
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
                CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.History.TABLE}
                    (
                        ${Database.History.COMMAND_ID}   TEXT,
                        ${Database.History.COMMAND_NAME} TEXT,
                        ${Database.History.COMMAND_DATE} TIMESTAMP,
                        ${Database.History.JSON_DATA}    TEXT,
                        PRIMARY KEY (${Database.History.COMMAND_ID})
                    );
            """
        )
    }
}
