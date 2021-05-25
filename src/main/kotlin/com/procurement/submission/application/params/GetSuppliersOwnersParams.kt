package com.procurement.submission.application.params

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

class GetSuppliersOwnersParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: String,
        val suppliers: List<Supplier>
    ) {
        data class Supplier(
            val id: String
        )
    }
}