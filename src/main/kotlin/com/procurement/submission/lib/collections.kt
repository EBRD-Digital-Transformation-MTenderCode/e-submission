package com.procurement.submission.lib

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