package com.procurement.submission.infrastructure.repository.history.model

import com.procurement.submission.infrastructure.api.Action
import com.procurement.submission.infrastructure.api.CommandId
import java.time.LocalDateTime

data class HistoryEntity(
    var commandId: CommandId,
    var action: Action,
    var date: LocalDateTime,
    var data: String
)
