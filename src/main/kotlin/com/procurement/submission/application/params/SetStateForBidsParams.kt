package com.procurement.submission.application.params


import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod

data class SetStateForBidsParams(
     val cpid: Cpid,
     val ocid: Ocid,
     val bids: Bids,
     val pmd: ProcurementMethod,
     val country: String,
     val operationType: OperationType
) {
    data class Bids(
         val details: List<Detail>
    ) {
        data class Detail(
             val id: BidId
        )
    }
}