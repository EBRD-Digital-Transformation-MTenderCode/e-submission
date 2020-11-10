package com.procurement.submission.infrastructure.handler

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.Action
import com.procurement.submission.infrastructure.api.CommandId
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.lib.functional.Result

interface HistoryRepository {
    fun getHistory(commandId: CommandId, action: Action): Result<String?, Fail.Incident.Database.Interaction>
    fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, Fail.Incident.Database.Interaction>
}
