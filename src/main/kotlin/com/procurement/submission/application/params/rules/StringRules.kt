package com.procurement.submission.application.params.rules

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.ValidationRule

fun notEmptyOrBlankRule(attributeName: String): ValidationRule<String, DataErrors.Validation> =
    ValidationRule { received: String ->
        if (received.isEmpty() || received.isBlank())
            Validated.error(DataErrors.Validation.EmptyString(attributeName))
        else
            Validated.ok()
    }