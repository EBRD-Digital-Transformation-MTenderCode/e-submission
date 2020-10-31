package com.procurement.submission.application.repository

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.model.entity.BidEntityComplex

interface BidRepository {
    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntityComplex>, Fail.Incident>
    fun saveAll(bidEntities: List<BidEntityComplex>): MaybeFail<Fail.Incident>}
