package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BidsUpdateStatusDetailsResponseDto(

        @JsonProperty("bid")
        val bid: BidUpdateDto
)
