package com.procurement.submission.infrastructure.repository.period

import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.period.PeriodRepository
import com.procurement.submission.application.repository.period.model.PeriodEntity
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.submission.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraPeriodRepository(private val session: Session) : PeriodRepository {

    companion object {

        private const val SAVE_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.Period.TABLE}
               (
                   ${Database.Period.CPID},
                   ${Database.Period.OCID},
                   ${Database.Period.START_DATE},
                   ${Database.Period.END_DATE}
               )
               VALUES ( ?, ?, ?, ? )
            """

        private const val GET_CQL = """
               SELECT ${Database.Period.START_DATE},
                      ${Database.Period.END_DATE}
                 FROM ${Database.KEYSPACE}.${Database.Period.TABLE}
                WHERE ${Database.Period.CPID}=?
                  AND ${Database.Period.OCID}=?
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedGetCQL = session.prepare(GET_CQL)

    override fun save(entity: PeriodEntity): MaybeFail<Fail.Incident.Database.Interaction> {
        preparedSaveCQL.bind()
            .apply {
                setString(Database.Period.CPID, entity.cpid.toString())
                setString(Database.Period.OCID, entity.ocid.toString())
                setTimestamp(Database.Period.START_DATE, entity.startDate.toCassandraTimestamp())
                setTimestamp(Database.Period.END_DATE, entity.endDate.toCassandraTimestamp())
            }
            .tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }
        return MaybeFail.none()
    }

    override fun find(cpid: Cpid, ocid: Ocid): Result<PeriodEntity?, Fail.Incident.Database.Interaction> =
        preparedGetCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                PeriodEntity(
                    cpid = cpid,
                    ocid = ocid,
                    startDate = row.getTimestamp(Database.Period.START_DATE).toLocalDateTime(),
                    endDate = row.getTimestamp(Database.Period.END_DATE).toLocalDateTime()
                )
            }
            .asSuccess()
}
