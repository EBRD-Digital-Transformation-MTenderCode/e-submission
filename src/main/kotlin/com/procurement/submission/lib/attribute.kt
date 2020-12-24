package com.procurement.submission.lib

import com.procurement.submission.application.exception.ErrorException

inline fun <T : String?, E : RuntimeException> T.takeIfNotEmpty(error: () -> E): T =
    if (this != null && this.isBlank()) throw error() else this

fun <T> T?.takeIfNotNullOrDefault(default: T?): T? = this ?: default

inline fun <T : String?> T.errorIfBlank(error: () -> ErrorException): T =
    if (this != null && this.isBlank()) throw error() else this
