package com.procurement.submission.infrastructure.repository.rule

import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.rule.RuleRepository
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraRuleRepository(private val session: Session) : RuleRepository {

    companion object {

        private const val ALL_OPERATION_TYPE = "all"

        private const val GET_VALUE_BY_CQL = """
               SELECT ${Database.Rules.VALUE}
                 FROM ${Database.KEYSPACE}.${Database.Rules.TABLE}
                WHERE ${Database.Rules.COUNTRY}=? 
                  AND ${Database.Rules.PMD}=?
                  AND ${Database.Rules.OPERATION_TYPE}=?
                  AND ${Database.Rules.PARAMETER}=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_BY_CQL)

    override fun find(
        country: String,
        pmd: ProcurementMethod,
        parameter: String,
        operationType: OperationType?
    ): Result<String?, Fail.Incident.Database.Interaction> =
        preparedGetValueByCQL.bind()
            .apply {
                setString(Database.Rules.COUNTRY, country)
                setString(Database.Rules.PMD, pmd.name)
                setString(Database.Rules.OPERATION_TYPE, operationType?.key ?: ALL_OPERATION_TYPE)
                setString(Database.Rules.PARAMETER, parameter)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.Rules.VALUE)
            .asSuccess()
}
