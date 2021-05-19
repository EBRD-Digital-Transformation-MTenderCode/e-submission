package com.procurement.submission.application.service

import com.procurement.submission.application.params.GetSuppliersOwnersParams
import com.procurement.submission.application.params.bid.query.find.FindDocumentsByBidIdsParams
import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsParams
import com.procurement.submission.application.params.bid.query.get.GetOrganizationsByReferencesFromPacsParams
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetOrganizationsByReferencesFromPacsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetSuppliersOwnersResponse
import com.procurement.submission.lib.functional.Result

interface BidQueryService {

    fun getBidsForPacs(params: GetBidsForPacsParams): Result<GetBidsForPacsResult, Fail>

    fun getOrganizationsByReferencesFromPacs(params: GetOrganizationsByReferencesFromPacsParams): Result<GetOrganizationsByReferencesFromPacsResult, Fail>

    fun findDocumentByBidIds(params: FindDocumentsByBidIdsParams): Result<FindDocumentsByBidIdsResult?, Fail>

    fun getSuppliersOwners(params: GetSuppliersOwnersParams): Result<GetSuppliersOwnersResponse, Fail>
}