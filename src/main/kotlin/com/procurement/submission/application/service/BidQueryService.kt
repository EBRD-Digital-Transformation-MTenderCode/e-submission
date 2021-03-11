package com.procurement.submission.application.service

import com.procurement.submission.application.params.bid.query.find.FindDocumentsByBidIdsParams
import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsParams
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import com.procurement.submission.lib.functional.Result

interface BidQueryService {

    fun getBidsForPacs(params: GetBidsForPacsParams): Result<GetBidsForPacsResult, Fail>

    fun findDocumentByBidIds(params: FindDocumentsByBidIdsParams): Result<FindDocumentsByBidIdsResult?, Fail>
}