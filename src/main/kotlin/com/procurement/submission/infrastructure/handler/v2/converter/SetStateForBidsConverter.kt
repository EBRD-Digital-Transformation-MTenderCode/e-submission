package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.SetStateForBidsParams
import com.procurement.submission.application.params.parseBidId
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.parseOperationType
import com.procurement.submission.application.params.parsePmd
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.handler.v2.model.request.SetStateForBidsRequest
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess

private val allowedPmds = ProcurementMethod.values()
    .filter {
        when (it) {
            ProcurementMethod.CF, ProcurementMethod.TEST_CF,
            ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
            ProcurementMethod.MV, ProcurementMethod.TEST_MV,
            ProcurementMethod.OF, ProcurementMethod.TEST_OF,
            ProcurementMethod.OT, ProcurementMethod.TEST_OT,
            ProcurementMethod.RFQ, ProcurementMethod.TEST_RFQ,
            ProcurementMethod.RT, ProcurementMethod.TEST_RT,
            ProcurementMethod.SV, ProcurementMethod.TEST_SV -> true

            ProcurementMethod.CD, ProcurementMethod.TEST_CD,
            ProcurementMethod.DA, ProcurementMethod.TEST_DA,
            ProcurementMethod.DC, ProcurementMethod.TEST_DC,
            ProcurementMethod.FA, ProcurementMethod.TEST_FA,
            ProcurementMethod.IP, ProcurementMethod.TEST_IP,
            ProcurementMethod.NP, ProcurementMethod.TEST_NP,
            ProcurementMethod.OP, ProcurementMethod.TEST_OP -> false
        }
    }.toSet()

private val allowedOperationType = OperationType.values()
    .filter {
        when (it) {
            OperationType.WITHDRAW_BID -> true

            OperationType.COMPLETE_QUALIFICATION,
            OperationType.CREATE_PCR,
            OperationType.CREATE_RFQ,
            OperationType.QUALIFICATION_PROTOCOL,
            OperationType.START_SECOND_STAGE,
            OperationType.SUBMIT_BID_IN_PCR -> false
        }
    }.toSet()

fun SetStateForBidsRequest.convert(): Result<SetStateForBidsParams, DataErrors> =  SetStateForBidsParams(
    cpid = parseCpid(cpid).onFailure { return it },
    ocid = parseOcid(ocid).onFailure { return it },
    pmd = parsePmd(pmd, allowedPmds).onFailure { return it },
    country = country,
    operationType = parseOperationType(operationType, allowedOperationType).onFailure { return it },
    bids = bids.convert().onFailure { return it }
).asSuccess()


fun SetStateForBidsRequest.Bids.convert(): Result<SetStateForBidsParams.Bids, DataErrors> = SetStateForBidsParams.Bids(
    details = details.map { detail ->
        SetStateForBidsParams.Bids.Detail(
            id = parseBidId(detail.id, "bids.details.id").onFailure { return it }
        )
    }
).asSuccess()