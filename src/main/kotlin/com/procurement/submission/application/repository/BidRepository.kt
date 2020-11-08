package com.procurement.submission.application.repository

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.model.entity.BidEntityComplex

interface BidRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntityComplex>, Fail.Incident>
    fun findBy(cpid: Cpid, ocid: Ocid, id: BidId): Result<BidEntityComplex?, Fail.Incident>
    fun saveNew(bidEntity: BidEntityComplex): MaybeFail<Fail.Incident>
    fun saveNew(bidEntities: List<BidEntityComplex>): MaybeFail<Fail.Incident>
}
