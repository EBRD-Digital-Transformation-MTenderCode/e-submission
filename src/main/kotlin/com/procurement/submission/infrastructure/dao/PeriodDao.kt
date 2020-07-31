package com.procurement.submission.infrastructure.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
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
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)

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
}
