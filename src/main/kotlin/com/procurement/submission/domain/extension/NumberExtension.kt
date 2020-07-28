package com.procurement.submission.domain.extension

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess

fun String.tryToLong(): Result<Long, Fail.Incident.Transform.Parsing> = try {
    this.toLong().asSuccess()
} catch (exception: NumberFormatException) {
    Fail.Incident.Transform.Parsing(className = Long::class.java.canonicalName, exception = exception).asFailure()
}