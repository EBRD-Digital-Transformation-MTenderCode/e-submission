package com.procurement.submission.application.params.rules

import com.procurement.submission.domain.extension.getDuplicate
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.ValidationRule

fun <T : Collection<Any>?> noDuplicatesRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        val duplicate = received?.getDuplicate { it }
        if (duplicate != null)
            Validated.error(
                DataErrors.Validation.UniquenessDataMismatch(
                    value = duplicate.toString(), name = attributeName
                )
            )
        else
            Validated.ok()
    }

fun <T : Collection<Any>?> notEmptyRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        if (received != null && received.isEmpty())
            Validated.error(DataErrors.Validation.EmptyArray(attributeName))
        else
            Validated.ok()
    }