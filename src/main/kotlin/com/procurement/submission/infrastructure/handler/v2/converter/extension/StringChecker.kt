package com.procurement.submission.infrastructure.handler.v2.converter.extension

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asValidationError

fun String?.checkForBlank(path: String): Validated<DataErrors.Validation.EmptyString> =
    if (this != null && this.isBlank())
        DataErrors.Validation.EmptyString(path).asValidationError()
    else
        Validated.ok()
