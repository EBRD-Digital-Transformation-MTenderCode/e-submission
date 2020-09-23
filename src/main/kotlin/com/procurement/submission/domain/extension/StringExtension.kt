package com.procurement.submission.domain.extension

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
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

fun String.tryToBoolean(): Result<Boolean, Fail.Incident.Transform.Parsing> =
    when {
        this.equals("true", ignoreCase = true) -> true.asSuccess()
        this.equals("false", ignoreCase = true) -> false.asSuccess()
        else -> Fail.Incident.Transform.Parsing(className = Boolean::class.java.canonicalName).asFailure()
    }


fun main(){
    val n = "test".toBoolean()
    println(n)
}