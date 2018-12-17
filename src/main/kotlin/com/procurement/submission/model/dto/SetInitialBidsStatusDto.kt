package com.procurement.submission.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.*
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialBidsStatusDtoRq @JsonCreator constructor(
    val awards: Set<SetInitialAward>,
    val firstBids: FirstBid
)

data class SetInitialAward @JsonCreator constructor(val id: String)

data class FirstBid @JsonCreator constructor(val id: String)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialBidsStatusDtoRs @JsonCreator constructor(
    val bids: SetInitialBidRs
)

data class SetInitialBidRs @JsonCreator constructor(
    val details: List<BidDetails>
)

data class BidDetails @JsonCreator constructor(
    var id: String,

    var date: LocalDateTime,

    var status: Status,

    var statusDetails: StatusDetails,

    val tenderers: List<OrganizationReference>,

    var value: Value,

    var documents: List<Document>,

    val relatedLots: List<String>

)
