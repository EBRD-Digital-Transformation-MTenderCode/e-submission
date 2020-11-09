package com.procurement.submission.domain.extension

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import java.util.*

fun String.tryUUID(): Result<UUID, Fail.Incident.Transform.Parsing> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            Fail.Incident.Transform.Parsing(UUID::class.java.canonicalName, ex)
        )
    }

fun String.tryToLong(): Result<Long, Fail.Incident.Transform.Parsing> = try {
    this.toLong().asSuccess()
} catch (exception: NumberFormatException) {
    Fail.Incident.Transform.Parsing(className = Long::class.java.canonicalName, exception = exception).asFailure()
}

private val TRUE = Result.Success(true)
private val FALSE = Result.Success(false)

fun String.tryToBoolean(): Result<Boolean, Fail.Incident.Transform.Parsing> =
    when {
        this.equals("true", ignoreCase = true) -> TRUE
        this.equals("false", ignoreCase = true) -> FALSE
        else -> Result.failure(
            Fail.Incident.Transform.Parsing(
                className = Boolean::class.java.canonicalName,
                exception = IllegalArgumentException("Invalid value '$this'.")
            )
        )
    }