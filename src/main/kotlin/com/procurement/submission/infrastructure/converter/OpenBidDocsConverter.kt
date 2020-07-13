package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsData
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsResult
import com.procurement.submission.infrastructure.dto.bid.opendoc.request.OpenBidDocsRequest
import com.procurement.submission.infrastructure.dto.bid.opendoc.response.OpenBidDocsResponse

fun OpenBidDocsRequest.convert() = OpenBidDocsData(
    bidId = this.bidId
)

fun OpenBidDocsResult.convert() = OpenBidDocsResponse(
    bid = this.bid.let { bid ->
        OpenBidDocsResponse.Bid(
            id = bid.id,
            documents = bid.documents
                .map { document ->
                    OpenBidDocsResponse.Bid.Document(
                        id = document.id,
                        description = document.description,
                        documentType = document.documentType,
                        relatedLots = document.relatedLots.toList(),
                        title = document.title
                    )
                }
        )
    }
)
