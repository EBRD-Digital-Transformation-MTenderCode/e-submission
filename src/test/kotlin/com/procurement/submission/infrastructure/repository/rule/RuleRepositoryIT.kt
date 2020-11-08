package com.procurement.submission.infrastructure.repository.rule

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
import com.procurement.submission.application.repository.rule.RuleRepository
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.get
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.lib.functional.Result
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class RuleRepositoryIT {
    companion object {
        private const val PARAMETER = "someParameter"
        private const val COUNTRY = "MD"
        private val PMD = ProcurementMethod.GPA
        private val OPERATION_TYPE = OperationType.START_SECOND_STAGE
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var ruleRepository: RuleRepository

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

        ruleRepository = CassandraRuleRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getValue_success() {
        val value = "10"
        insertRule(COUNTRY, PMD, PARAMETER, OPERATION_TYPE, value)
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertEquals(value, actual)
    }

    @Test
    fun getValue_noValueFound_success() {
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertTrue(actual == null)
    }

    @Test
    fun tryGetValue_success() {
        val value = "10"
        insertRule(COUNTRY, PMD, PARAMETER, OPERATION_TYPE, value)
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertEquals(value, actual)
    }

    @Test
    fun tryGetValue_noValueFound_success() {
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertTrue(actual == null)
    }

    @Test
    fun findBy_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE)

        assertTrue(expected is Result.Failure)
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
                CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.Rules.TABLE}
                    (
                        ${Database.Rules.COUNTRY}        TEXT,
                        ${Database.Rules.PMD}            TEXT,
                        ${Database.Rules.OPERATION_TYPE} TEXT,
                        ${Database.Rules.PARAMETER}      TEXT,
                        ${Database.Rules.VALUE}          TEXT,
                        PRIMARY KEY (${Database.Rules.COUNTRY}, ${Database.Rules.PMD}, ${Database.Rules.OPERATION_TYPE}, ${Database.Rules.PARAMETER})
                    );
            """
        )
    }

    private fun insertRule(
        country: String,
        pmd: ProcurementMethod,
        parameter: String,
        operationType: OperationType,
        value: String
    ) {
        val record = QueryBuilder.insertInto(Database.KEYSPACE, Database.Rules.TABLE)
            .value(Database.Rules.COUNTRY, country)
            .value(Database.Rules.PMD, pmd.name)
            .value(Database.Rules.OPERATION_TYPE, operationType.toString())
            .value(Database.Rules.PARAMETER, parameter)
            .value(Database.Rules.VALUE, value)

        session.execute(record)
    }
}