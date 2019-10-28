package com.procurement.submission.domain.model

import kotlin.jvm.internal.Intrinsics

interface Entity<ID> {
    val id: ID

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

abstract class EntityBase<ID> : Entity<ID> {
    override fun equals(other: Any?): Boolean {
        return if (this !== other) {
            other is EntityBase<*> && Intrinsics.areEqual(this.id, other.id)
        } else
            true
    }

    override fun hashCode(): Int = id.hashCode()
}

fun <ID> Iterable<Entity<ID>>.isUniqueIds(): Boolean {
    val unique = HashSet<ID>()
    forEach { item ->
        if (!unique.add(item.id)) return false
    }
    return true
}

fun <ID> Iterable<Entity<ID>>.isNotUniqueIds(): Boolean = !this.isUniqueIds()

inline fun <ID, E : Entity<ID>, T : Exception> Iterable<E>.isNotUniqueIds(block: (E) -> T) {
    val unique = HashSet<ID>()
    forEach { item ->
        if (!unique.add(item.id)) throw block(item)
    }
}
