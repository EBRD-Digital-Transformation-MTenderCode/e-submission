package com.procurement.submission.domain.extension

import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.fail.Fail
import java.util.*

fun String.tryUUID(): Result<UUID, Fail.Incident.Transform.Parsing> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            Fail.Incident.Transform.Parsing(UUID::class.java.canonicalName, ex)
        )
    }