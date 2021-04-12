package com.procurement.submission.application.params


import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.bid.BidId

data class CheckExistenceOfInvitationParams(
    val cpid: Cpid,
    val bids: Bids
) {
    data class Bids(
         val details: List<Detail>
    ) {
        data class Detail(
             val id: BidId,
             val tenderers: List<Tenderer>
        ) {
            data class Tenderer(
                 val id: String
            )
        }
    }
}