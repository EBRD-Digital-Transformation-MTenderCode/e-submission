package com.procurement.submission.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.HistoryRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.extension.nowDefaultUTC
import com.procurement.submission.domain.extension.toDate
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.entity.HistoryEntity
import org.springframework.stereotype.Repository

@Repository
class HistoryRepositoryCassandra(private val session: Session, private val transform: Transform) : HistoryRepository {

    companion object {
        private const val KEYSPACE = "ocds"
        private const val HISTORY_TABLE = "submission_history"
        private const val OPERATION_ID = "operation_id"
        private const val COMMAND = "command"
        private const val COMMAND_DATE = "operation_date"
        const val JSON_DATA = "json_data"

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO $KEYSPACE.$HISTORY_TABLE(
                      $OPERATION_ID,
                      $COMMAND,
                      $COMMAND_DATE,
                      $JSON_DATA
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT $OPERATION_ID,
                      $COMMAND,
                      $COMMAND_DATE,
                      $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $OPERATION_ID=?
                  AND $COMMAND=?
               LIMIT 1
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident> {
        val query = preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(OPERATION_ID, operationId)
                setString(COMMAND, command)
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                HistoryEntity(
                    row.getString(OPERATION_ID),
                    row.getString(COMMAND),
                    row.getTimestamp(COMMAND_DATE),
                    row.getString(JSON_DATA)
                )
            }
            .asSuccess()
    }

    override fun saveHistory(operationId: String, command: String, result: Any): Result<HistoryEntity, Fail.Incident> {
        val entity = HistoryEntity(
            operationId = operationId,
            command = command,
            operationDate = nowDefaultUTC().toDate(),
            jsonData = transform.trySerialization(result).onFailure { return it }
        )

        val insert = preparedSaveHistoryCQL.bind()
            .apply {
                setString(OPERATION_ID, entity.operationId)
                setString(COMMAND, entity.command)
                setTimestamp(COMMAND_DATE, entity.operationDate)
                setString(JSON_DATA, entity.jsonData)
            }

        insert.tryExecute(session).onFailure { return it }

        return entity.asSuccess()
    }
}
