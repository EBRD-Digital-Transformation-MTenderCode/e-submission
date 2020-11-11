package com.procurement.submission.application.repository.rule

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.lib.functional.Result

interface RuleRepository {
    fun find(
        country: String,
        pmd: ProcurementMethod,
        parameter: String,
        operationType: OperationType? = null
    ): Result<String?, Fail.Incident.Database.Interaction>
}
