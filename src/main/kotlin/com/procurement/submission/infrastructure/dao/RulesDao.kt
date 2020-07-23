package com.procurement.submission.infrastructure.dao

import com.datastax.driver.core.Session
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import org.springframework.stereotype.Service

@Service
class RulesDao(private val session: Session) {

    companion object {

        private const val RULES_TABLE = "submission_rules"
        private const val KEYSPACE = "ocds"
        private const val COUNTRY_COLUMN = "country"
        private const val PMD_COLUMN = "pmd"
        private const val OPERATION_TYPE_COLUMN = "operation_type"
        private const val PARAMETER_COLUMN = "parameter"
        private const val VALUE_COLUMN = "value"
        private const val ALL_OPERATION_TYPE = "all"

        private const val GET_VALUE_BY_CQL = """
               SELECT $VALUE_COLUMN
                 FROM $KEYSPACE.$RULES_TABLE
                WHERE $COUNTRY_COLUMN=? 
                  AND $PMD_COLUMN=?
                  AND $OPERATION_TYPE_COLUMN=?
                  AND $PARAMETER_COLUMN=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_BY_CQL)

    fun getValue(country: String, pmd: String, parameter: String, operationType: String = ALL_OPERATION_TYPE): String? {
        val query = preparedGetValueByCQL.bind()
            .apply {
                setString(COUNTRY_COLUMN, country)
                setString(PMD_COLUMN, pmd)
                setString(OPERATION_TYPE_COLUMN, operationType)
                setString(PARAMETER_COLUMN, parameter)
            }
        return session
            .execute(query)
            .one()
            ?.getString(VALUE_COLUMN)
    }

    fun tryGetValue(
        country: String,
        pmd: ProcurementMethod,
        parameter: String,
        operationType: OperationType? = null
    ): Result<String?, Fail.Incident.Database.Interaction> {
        val query = preparedGetValueByCQL.bind()
            .apply {
                setString(COUNTRY_COLUMN, country)
                setString(PMD_COLUMN, pmd.name)
                setString(OPERATION_TYPE_COLUMN, operationType?.key ?: ALL_OPERATION_TYPE)
                setString(PARAMETER_COLUMN, parameter)
            }

        return query.tryExecute(session)
            .orForwardFail { fail -> return fail }
            .one()
            ?.getString(VALUE_COLUMN)
            .asSuccess()
    }
}
