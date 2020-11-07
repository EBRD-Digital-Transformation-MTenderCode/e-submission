package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess

class PublishInvitationsParams private constructor(
    val cpid: Cpid,
    val operationType: OperationType
) {
    companion object {

        val allowedOperationTypes = OperationType.allowedElements
            .filter {
                when (it) {
                    OperationType.CREATE_PCR,
                    OperationType.START_SECOND_STAGE -> true

                    OperationType.QUALIFICATION_PROTOCOL,
                    OperationType.SUBMIT_BID_IN_PCR -> false
                }
            }.toSet()

        fun tryCreate(cpid: String, operationType: String): Result<PublishInvitationsParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { fail -> return fail }

            val operationTypeParsed = parseOperationType(operationType, allowedOperationTypes)
                .orForwardFail { fail -> return fail }

            return PublishInvitationsParams(cpid = cpidParsed, operationType = operationTypeParsed)
                .asSuccess()
        }
    }
}