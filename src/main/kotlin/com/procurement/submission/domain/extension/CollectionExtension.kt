package com.procurement.submission.domain.extension

import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess

inline fun <T, V> Collection<T>.uniqueBy(selector: (T) -> V): Boolean {
    val unique = HashSet<V>()
    forEach { item ->
        if (!unique.add(selector(item))) return false
    }
    return true
}

inline fun <T, V> Collection<T>.toSetBy(selector: (T) -> V): Set<V> {
    val collections = LinkedHashSet<V>()
    forEach {
        collections.add(selector(it))
    }
    return collections
}

inline fun <T, R> Collection<T>.mapIfNotEmpty(transform: (T) -> R): List<R>? =
    if (this.isNotEmpty()) this.map(transform) else null

inline fun <T, C : Collection<T>, E : RuntimeException> C?.errorIfEmpty(exceptionBuilder: () -> E): C? =
    if (this != null && this.isEmpty()) throw exceptionBuilder() else this

inline fun <T, C : Collection<T>> C?.orElse(defaultBuilder: () -> C): C = this ?: defaultBuilder()

inline fun <T, reified C : Collection<T>, E : RuntimeException> C?.orThrow(exceptionBuilder: () -> E): C =
    this ?: throw exceptionBuilder()

fun <T, R, E> List<T>.mapResult(block: (T) -> Result<R, E>): Result<List<R>, E> = mutableListOf<R>()
    .apply {
        for (element in this@mapResult) {
            block(element)
                .onFailure { return it }
                .also { add(it) }
        }
    }
    .asSuccess()

fun <T> T?.toListOrEmpty(): List<T> = if (this != null) listOf(this) else emptyList()

fun <T> getMissingElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    known.asSet().subtract(received.asSet())

fun <T> getUnknownElements(received: Iterable<T>, known: Iterable<T>) =
    getNewElements(received = received, known = known)

fun <T> getNewElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    received.asSet().subtract(known.asSet())

private fun <T> Iterable<T>.asSet(): Set<T> = when (this) {
    is Set -> this
    else -> this.toSet()
}

inline fun <T, V> Collection<T>?.getDuplicate(selector: (T) -> V): T? {
    val unique = HashSet<V>()
    this?.forEach { item ->
        if (!unique.add(selector(item))) return item
    }
    return null
}

inline fun <T, V> Collection<T>.getDuplicated(selector: (T) -> V): List<V> =
    this.map { selector(it) }
        .groupBy { it }
        .filter { (_, used) -> used.size > 1  }
        .map { it.key }
