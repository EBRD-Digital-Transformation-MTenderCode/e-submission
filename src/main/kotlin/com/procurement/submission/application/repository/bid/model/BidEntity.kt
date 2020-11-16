package com.procurement.submission.application.repository.bid.model

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.model.dto.ocds.Bid
import java.time.LocalDateTime

sealed class BidEntity {
    abstract val cpid: Cpid
    abstract val ocid: Ocid
    abstract val createdDate: LocalDateTime
    abstract val pendingDate: LocalDateTime?

    class New(
        override val cpid: Cpid,
        override val ocid: Ocid,
        val token: Token,
        val owner: Owner,
        override val createdDate: LocalDateTime,
        override val pendingDate: LocalDateTime?,
        val bid: Bid
    ) : BidEntity()

    class Updated(
        override val cpid: Cpid,
        override val ocid: Ocid,
        override val createdDate: LocalDateTime,
        override val pendingDate: LocalDateTime?,
        val bid: Bid
    ) : BidEntity()

    class Record(
        val cpid: Cpid,
        val ocid: Ocid,
        val bidId: BidId,
        val token: Token,
        val owner: Owner,
        val status: Status,
        val createdDate: LocalDateTime,
        val pendingDate: LocalDateTime?,
        val jsonData: String
    )
}
