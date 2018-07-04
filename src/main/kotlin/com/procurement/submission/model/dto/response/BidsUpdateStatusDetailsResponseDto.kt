package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsUpdateStatusDetailsResponseDto @JsonCreator constructor(

        val bid: BidUpdateDto
)
