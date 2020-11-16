package com.procurement.submission.application.repository.period

import com.procurement.submission.application.repository.period.model.PeriodEntity
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result

interface PeriodRepository {
    fun save(entity: PeriodEntity): MaybeFail<Fail.Incident.Database.Interaction>
    fun find(cpid: Cpid, ocid: Ocid): Result<PeriodEntity?, Fail.Incident.Database.Interaction>
}
