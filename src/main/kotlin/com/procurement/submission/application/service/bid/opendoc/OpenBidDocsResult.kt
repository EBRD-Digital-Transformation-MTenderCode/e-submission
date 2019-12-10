package com.procurement.submission.application.service.bid.opendoc

import com.procurement.submission.domain.model.DocumentId
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.lot.LotId

class OpenBidDocsResult(
    val bid: Bid
) {
    data class Bid(
        val documents: List<Document>,
        val id: BidId
    ) {
        data class Document(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>
        )
    }
}
