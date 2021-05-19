package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.GetSuppliersOwnersParams
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.request.GetSuppliersOwnersRequest
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

fun GetSuppliersOwnersRequest.convert(): Result<GetSuppliersOwnersParams, Fail> {
    val contracts = contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }
        .mapResult { it.convert() }
        .onFailure { return it }

    return GetSuppliersOwnersParams(
        cpid = parseCpid(cpid).onFailure { return it },
        ocid = parseOcid(ocid).onFailure { return it },
        contracts = contracts
    ).asSuccess()
}

fun GetSuppliersOwnersRequest.Contract.convert(): Result<GetSuppliersOwnersParams.Contract, Fail> {
    val suppliers = suppliers.validate(notEmptyRule("contracts.suppliers"))
        .onFailure { return it }
        .mapResult { it.convert() }
        .onFailure { return it }


    return GetSuppliersOwnersParams.Contract(
        id = id,
        suppliers = suppliers
    ).asSuccess()
}

fun GetSuppliersOwnersRequest.Contract.Supplier.convert(): Result<GetSuppliersOwnersParams.Contract.Supplier, Fail> =
    GetSuppliersOwnersParams.Contract.Supplier(
        id = id
    ).asSuccess()
