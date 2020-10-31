package com.procurement.submission.utils

import com.procurement.submission.domain.fail.Fail.Error.Companion.toValidationResult
import com.procurement.submission.domain.fail.error.DomainErrors
import com.procurement.submission.domain.functional.ValidationResult
import com.procurement.submission.domain.functional.ValidationRule
import java.math.BigDecimal

fun scaleValidationRule(className: String, availableScale: Int) =
    ValidationRule { value: BigDecimal ->
        val scale = value.scale()
        if (scale > availableScale)
            DomainErrors.InvalidScale(className = className, currentScale = scale, availableScale = availableScale)
                .toValidationResult()
        else
            ValidationResult.ok()
    }

fun negativeValidationRule(className: String) = ValidationRule { amount: BigDecimal ->
    if (amount < BigDecimal.ZERO)
        DomainErrors.IncorrectValue(className = className, value = amount, reason = "The value must not be negative.")
            .toValidationResult()
    else
        ValidationResult.ok()
}
