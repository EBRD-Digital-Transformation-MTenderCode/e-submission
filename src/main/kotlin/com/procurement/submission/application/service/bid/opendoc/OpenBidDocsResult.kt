package com.procurement.submission.application.service.bid.opendoc

import com.procurement.submission.domain.model.enums.DocumentType

class OpenBidDocsResult(
    val bid: Bid
) {
    data class Bid(
        val documents: List<Document>,
        val id: String
    ) {
        data class Document(
            val documentType: DocumentType,
            val id: String,
            val title: String?,
            val description: String?,
            val relatedLots: List<String>
        )
    }
}