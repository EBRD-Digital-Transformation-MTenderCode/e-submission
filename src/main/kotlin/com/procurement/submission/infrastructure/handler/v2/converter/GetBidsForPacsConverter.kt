package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsParams
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.infrastructure.handler.v2.model.request.GetBidsForPacsRequest
import com.procurement.submission.lib.functional.Result

fun GetBidsForPacsRequest.convert(): Result<GetBidsForPacsParams, DataErrors> =
    GetBidsForPacsParams.tryCreate(cpid = cpid, ocid = ocid, tender = tender.convert())

fun GetBidsForPacsRequest.Tender.convert(): GetBidsForPacsParams.Tender =
    GetBidsForPacsParams.Tender(lots = lots.map { it.convert() })

fun GetBidsForPacsRequest.Tender.Lot.convert(): GetBidsForPacsParams.Tender.Lot =
    GetBidsForPacsParams.Tender.Lot(id = id)
