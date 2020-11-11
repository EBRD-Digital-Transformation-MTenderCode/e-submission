package com.procurement.submission

import com.procurement.submission.lib.functional.Validated

fun <E> Validated<E>.error(): E = when (this) {
    is Validated.Ok -> throw IllegalArgumentException("Validated is not error.")
    is Validated.Error -> this.reason
}
