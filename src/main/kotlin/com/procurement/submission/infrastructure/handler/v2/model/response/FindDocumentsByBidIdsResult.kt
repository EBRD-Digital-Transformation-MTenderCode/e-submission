package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.Fail.Incident.Database.Consistency
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult.Bids
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Result.Companion.success
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Document

data class FindDocumentsByBidIdsResult(
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
        ) { companion object{}

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?,
            ) { companion object {} }
        }
    }
}

fun Bids.Detail.Companion.fromDomain(bid: Bid): Result<Bids.Detail, Consistency> {
    val path = "bids/details"
    val bidDetails = Bids.Detail(
        id = bid.id,
        documents = bid.documents.orEmpty()
            .mapResult { Bids.Detail.Document.fromDomain(it, "$path/documents") }
            .onFailure { return it }
    )

    return success(bidDetails)
}

fun Bids.Detail.Document.Companion.fromDomain(bid: Document, path: String): Result<Bids.Detail.Document, Consistency> {
    val document =  Bids.Detail.Document(
        id = bid.id,
        title = bid.title ?: return Consistency("Missing '$path/title' in DB").asFailure(),
        description = bid.description,
        documentType = bid.documentType.key,
        relatedLots = bid.relatedLots.orEmpty().toList()
    )
    return success(document)
}