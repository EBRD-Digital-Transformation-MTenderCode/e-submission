package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.bid.opendoc.OpenBidDocsData
import com.procurement.submission.application.service.bid.opendoc.OpenBidDocsResult
import com.procurement.submission.infrastructure.dto.bid.opendoc.request.OpenBidDocsRequest
import com.procurement.submission.infrastructure.dto.bid.opendoc.response.OpenBidDocsResponse
import com.procurement.submission.lib.mapIfNotEmpty

fun OpenBidDocsRequest.toData() = OpenBidDocsData(nextAwardForUpdate = this.nextAwardForUpdate.let { nextAwardForUpdate ->
    OpenBidDocsData.NextAwardForUpdate(
        id = nextAwardForUpdate.id,
        relatedBid = nextAwardForUpdate.relatedBid
    )
})

fun OpenBidDocsResult.toResponse() = OpenBidDocsResponse(
    bid = this.bid.let { bid ->
        OpenBidDocsResponse.Bid(
            id = bid.id,
            documents = bid.documents
                .mapIfNotEmpty { document ->
                    OpenBidDocsResponse.Bid.Document(
                        id = document.id,
                        description = document.description,
                        documentType = document.documentType,
                        relatedLots = document.relatedLots.toList(),
                        title = document.title
                    )
                }
                .orEmpty()
        )
    })