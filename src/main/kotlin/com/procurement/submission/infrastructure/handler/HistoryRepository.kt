package com.procurement.submission.infrastructure.handler

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.lib.functional.Result

interface HistoryRepository {
    fun getHistory(commandId: CommandId): Result<String?, Fail.Incident.Database.Interaction>
    fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, Fail.Incident.Database.Interaction>
}
