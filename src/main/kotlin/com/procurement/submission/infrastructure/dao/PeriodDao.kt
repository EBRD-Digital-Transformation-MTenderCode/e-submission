package com.procurement.submission.infrastructure.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.Stage
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.entity.PeriodEntity
import org.springframework.stereotype.Service

@Service
class PeriodDao(private val session: Session) {

    companion object {
        private const val PERIOD_TABLE = "submission_period"
        private const val KEYSPACE = "ocds"
        private const val CPID_COLUMN = "cp_id"
        private const val STAGE_COLUMN = "stage"
        private const val START_DATE_COLUMN = "start_date"
        private const val END_DATE_COLUMN = "end_date"

        private const val SAVE_CQL = """
               INSERT INTO $KEYSPACE.$PERIOD_TABLE
                     ($CPID_COLUMN,
                      $STAGE_COLUMN,
                      $START_DATE_COLUMN,
                      $END_DATE_COLUMN)
                  VALUES ( ?, ?, ?, ? )
            """

        private const val GET_CQL = """
               SELECT $START_DATE_COLUMN,
                      $END_DATE_COLUMN
                 FROM $KEYSPACE.$PERIOD_TABLE
                WHERE $CPID_COLUMN=?
                  AND $STAGE_COLUMN=?
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedGetCQL = session.prepare(GET_CQL)

    fun save(entity: PeriodEntity) {
        val insert =
                insertInto(PERIOD_TABLE)
                        .value(CPID_COLUMN, entity.cpId)
                        .value(STAGE_COLUMN, entity.stage)
                        .value(START_DATE_COLUMN, entity.startDate)
                        .value(END_DATE_COLUMN, entity.endDate)
        session.execute(insert)
    }

    fun getByCpIdAndStage(cpId: String, stage: String): PeriodEntity {
        val query = select()
                .all()
                .from(PERIOD_TABLE)
                .where(eq(CPID_COLUMN, cpId))
                .and(eq(STAGE_COLUMN, stage)).limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            PeriodEntity(
                cpId = row.getString(CPID_COLUMN),
                stage = row.getString(STAGE_COLUMN),
                startDate = row.getTimestamp(START_DATE_COLUMN),
                endDate = row.getTimestamp(END_DATE_COLUMN))
        else throw ErrorException(ErrorType.PERIOD_NOT_FOUND)
    }

    fun trySave(entity: PeriodEntity): MaybeFail<Fail.Incident> {
        val query = preparedSaveCQL.bind()
            .apply {
                setString(CPID_COLUMN, entity.cpId)
                setString(STAGE_COLUMN, entity.stage)
                setTimestamp(START_DATE_COLUMN, entity.startDate)
                setTimestamp(END_DATE_COLUMN, entity.endDate)
            }
        query.tryExecute(session)
            .doOnError { error -> return MaybeFail.fail(error) }

        return MaybeFail.none()
    }

    fun tryGetBy(cpid: Cpid, stage: Stage): Result<PeriodEntity?, Fail.Incident.Database.Interaction> {
        val query = preparedGetCQL.bind()
            .apply {
                setString(CPID_COLUMN, cpid.toString())
                setString(STAGE_COLUMN, stage.key)
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let {
                PeriodEntity(
                    cpId = cpid.toString(),
                    stage = stage.key,
                    startDate = it.getTimestamp(START_DATE_COLUMN),
                    endDate = it.getTimestamp(END_DATE_COLUMN)
                )
            }.asSuccess()
    }
}
