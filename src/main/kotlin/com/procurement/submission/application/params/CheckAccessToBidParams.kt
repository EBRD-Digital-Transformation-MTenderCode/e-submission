package com.procurement.submission.application.params

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId

data class CheckAccessToBidParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val bids: Bids,
    val token: Token,
    val owner: Owner
) {
    data class Bids(
        val details: List<Detail>
    ) {
        data class Detail(
            val id: BidId
        )
    }
}