package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.fail.error.DataErrors.Validation.DataMismatchToPattern
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.application.params.bid.query.find.FindDocumentsByBidIdsParams as Params
import com.procurement.submission.infrastructure.handler.v2.model.request.FindDocumentsByBidIdsRequest as Request

fun Request.convert(): Result<Params, DataErrors> =
    Params.tryCreate(
        cpid = cpid,
        ocid = ocid,
        bids = bids.convert().onFailure { return it }
    )

fun Request.Bids.convert(): Result<Params.Bids, DataMismatchToPattern> =
    Params.Bids(
        details = details
            .mapResult { it.convert() }
            .onFailure { return it }
    ).asSuccess()

fun Request.Bids.BidDetails.convert(): Result<Params.Bids.BidDetails, DataMismatchToPattern> =
    Params.Bids.BidDetails.tryCreate(id = id)
