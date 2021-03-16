package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.CheckAccessToBidParams
import com.procurement.submission.application.params.parseBidId
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.parseOwner
import com.procurement.submission.application.params.parseToken
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckAccessToBidRequest
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

fun CheckAccessToBidRequest.convert(): Result<CheckAccessToBidParams, DataErrors> = CheckAccessToBidParams(
    cpid = parseCpid(cpid).onFailure { return it },
    ocid = parseOcid(ocid).onFailure { return it },
    token = parseToken(token).onFailure { return it },
    owner = parseOwner(owner).onFailure { return it },
    bids = bids.convert().onFailure { return it }
).asSuccess()

fun CheckAccessToBidRequest.Bids.convert(): Result<CheckAccessToBidParams.Bids, DataErrors> = CheckAccessToBidParams.Bids(
    details = details
        .validate(notEmptyRule("bids.details"))
        .onFailure { return it }
        .map { detail ->
            CheckAccessToBidParams.Bids.Detail(
                id = parseBidId(detail.id, "bids.details.id").onFailure { return it }
            )
    }
).asSuccess()

