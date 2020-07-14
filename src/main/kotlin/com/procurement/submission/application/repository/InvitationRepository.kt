package com.procurement.submission.application.repository

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.invitation.Invitation

interface InvitationRepository {
    fun findBy(cpid: Cpid): Result<List<Invitation>, Fail.Incident>
    fun save(cpid: Cpid, invitation: Invitation): MaybeFail<Fail.Incident>
    fun saveAll(cpid: Cpid, invitations: List<Invitation>): MaybeFail<Fail.Incident>
}
