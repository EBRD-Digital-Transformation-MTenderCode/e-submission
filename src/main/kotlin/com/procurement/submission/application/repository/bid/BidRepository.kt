package com.procurement.submission.application.repository.bid

import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result

interface BidRepository {
    fun findBy(cpid: Cpid): Result<List<BidEntity.Record>, Fail.Incident.Database>
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntity.Record>, Fail.Incident.Database>
    fun findBy(cpid: Cpid, ocid: Ocid, id: BidId): Result<BidEntity.Record?, Fail.Incident.Database>
    fun findBy(cpid: Cpid, ocid: Ocid, ids: List<BidId>): Result<List<BidEntity.Record>, Fail.Incident.Database>
    fun save(bidEntity: BidEntity): MaybeFail<Fail.Incident.Database>
    fun save(bidEntities: Collection<BidEntity>): MaybeFail<Fail.Incident.Database>
}
