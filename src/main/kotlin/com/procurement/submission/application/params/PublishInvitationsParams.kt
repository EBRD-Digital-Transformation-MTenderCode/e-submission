package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid

class PublishInvitationsParams private constructor(
    val cpid: Cpid
) {
    companion object {

        fun tryCreate(cpid: String): Result<PublishInvitationsParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { fail -> return fail }

            return PublishInvitationsParams(cpid = cpidParsed)
                .asSuccess()
        }
    }
}