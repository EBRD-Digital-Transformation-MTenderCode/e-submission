package com.procurement.submission.infrastructure.dao

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
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.config.CassandraTestContainer
import com.procurement.submission.infrastructure.config.DatabaseTestConfiguration
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
class RulesDaoIT {
    companion object {
        private const val RULES_TABLE = "submission_rules"
        private const val KEYSPACE = "ocds"
        private const val COUNTRY_COLUMN = "country"
        private const val PMD_COLUMN = "pmd"
        private const val OPERATION_TYPE_COLUMN = "operation_type"
        private const val PARAMETER_COLUMN = "parameter"
        private const val VALUE_COLUMN = "value"

        private const val PARAMETER = "someParameter"
        private const val COUNTRY = "MD"
        private val PMD = ProcurementMethod.GPA
        private val OPERATION_TYPE = OperationType.START_SECOND_STAGE
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var rulesDao: RulesDao

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

        rulesDao = RulesDao(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getValue_success() {
        val value = "10"
        insertRule(COUNTRY, PMD, PARAMETER, OPERATION_TYPE, value)
        val actual = rulesDao.getValue(COUNTRY, PMD.name, PARAMETER, OPERATION_TYPE.key)

        assertEquals(value, actual)
    }

    @Test
    fun getValue_noValueFound_success() {
        val actual = rulesDao.getValue(COUNTRY, PMD.name, PARAMETER, OPERATION_TYPE.key)

        assertTrue(actual == null)
    }

    @Test
    fun tryGetValue_success() {
        val value = "10"
        insertRule(COUNTRY, PMD, PARAMETER, OPERATION_TYPE, value)
        val actual = rulesDao.tryGetValue(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get

        assertEquals(value, actual)
    }

    @Test
    fun tryGetValue_noValueFound_success() {
        val actual = rulesDao.tryGetValue(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get

        assertTrue(actual == null)
    }

    @Test
    fun findBy_executeException_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = rulesDao.tryGetValue(COUNTRY, PMD, PARAMETER, OPERATION_TYPE)

        assertTrue(expected is Result.Failure)
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE ${KEYSPACE} " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ${KEYSPACE};")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS ${KEYSPACE}.${RULES_TABLE}
                    (
                        $COUNTRY_COLUMN        text,
                        $PMD_COLUMN            text,
                        $OPERATION_TYPE_COLUMN text,
                        $PARAMETER_COLUMN      text,
                        $VALUE_COLUMN          text,
                        primary key ($COUNTRY_COLUMN, $PMD_COLUMN, $OPERATION_TYPE_COLUMN, $PARAMETER_COLUMN)
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
        val record = QueryBuilder.insertInto(KEYSPACE, RULES_TABLE)
            .value(COUNTRY_COLUMN, country)
            .value(PMD_COLUMN, pmd.name)
            .value(OPERATION_TYPE_COLUMN, operationType.toString())
            .value(PARAMETER_COLUMN, parameter)
            .value(VALUE_COLUMN, value)


        session.execute(record)
    }
}