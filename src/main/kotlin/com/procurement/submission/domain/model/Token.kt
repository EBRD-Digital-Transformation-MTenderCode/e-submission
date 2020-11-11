package com.procurement.submission.domain.model

import com.procurement.submission.domain.extension.tryUUID
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.lib.functional.Result
import java.util.*

typealias Token = UUID

fun String.tryToken(): Result<Token, DataErrors.Validation.DataFormatMismatch> =
    when (val result = this.tryUUID()) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(
            DataErrors.Validation.DataFormatMismatch(
                name = "token",
                actualValue = this,
                expectedFormat = "uuid"
            )
        )
    }

