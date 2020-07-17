package com.procurement.submission.application.repository

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.model.entity.HistoryEntity

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident>
    fun saveHistory(operationId: String, command: String, result: Any): Result<HistoryEntity, Fail.Incident>
}
