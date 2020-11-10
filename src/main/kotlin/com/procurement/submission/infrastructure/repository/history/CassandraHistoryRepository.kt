package com.procurement.submission.infrastructure.repository.history

import com.datastax.driver.core.Session
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraHistoryRepository(private val session: Session) : HistoryRepository {

    companion object {

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.History.TABLE}(
                      ${Database.History.COMMAND_ID},
                      ${Database.History.COMMAND_NAME},
                      ${Database.History.COMMAND_DATE},
                      ${Database.History.JSON_DATA}
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT ${Database.History.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.History.TABLE}
                WHERE ${Database.History.COMMAND_ID}=?
                  AND ${Database.History.COMMAND_NAME}=?
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId, action: Action): Result<String?, Fail.Incident.Database.Interaction> =
        preparedFindHistoryCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, commandId.underlying)
                setString(Database.History.COMMAND_NAME, action.key)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.History.JSON_DATA)
            .asSuccess()

    override fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, Fail.Incident.Database.Interaction> {

        preparedSaveHistoryCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, entity.commandId.underlying)
                setString(Database.History.COMMAND_NAME, entity.action.key)
                setTimestamp(Database.History.COMMAND_DATE, entity.date.toCassandraTimestamp())
                setString(Database.History.JSON_DATA, entity.data)
            }
            .tryExecute(session)
            .onFailure { return it }

        return entity.asSuccess()
    }
}
