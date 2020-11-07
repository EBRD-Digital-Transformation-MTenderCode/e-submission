package com.procurement.submission.lib.functional

fun <T, E> T.asSuccess(): Result<T, E> = Result.success(this)
fun <T, E> E.asFailure(): Result<T, E> = Result.failure(this)

sealed class Result<out T, out E> {
    companion object {
        fun <T> success(value: T) = Success(value)
        fun <E> failure(error: E) = Failure(error)
    }

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    abstract val get: T
    abstract val error: E

    inline fun doOnError(block: (E) -> Unit): Result<T, E> = when (this) {
        is Success<T> -> this
        is Failure<E> -> {
            block(reason)
            this
        }
    }

    inline fun onFailure(f: (Failure<E>) -> Nothing): T = when (this) {
        is Success<T> -> value
        is Failure<E> -> f(this)
    }

    inline fun recovery(f: (E) -> @UnsafeVariance T): T = when (this) {
        is Success<T> -> value
        is Failure<E> -> f(this.reason)
    }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(value)
            is Failure -> Option.none()
        }

    val orNull: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
        }

    infix fun orThrow(block: (E) -> Exception): T = when (this) {
        is Success -> value
        is Failure -> throw block(reason)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    infix fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    infix fun <R> mapFailure(transform: (E) -> R): Result<T, R> = when (this) {
        is Success -> this
        is Failure -> Failure(transform(reason))
    }

    fun forEach(block: (T) -> Unit): Unit = when (this) {
        is Success -> block(value)
        is Failure -> Unit
    }

    class Success<out T> internal constructor(val value: T) : Result<T, Nothing>() {
        override val isSuccess: Boolean = true
        override val isFailure: Boolean = false
        override val get: T = value
        override val error: Nothing
            get() = throw NoSuchElementException("The result does not contain an error.")

        override fun toString(): String = "Success($value)"
    }

    class Failure<out E> internal constructor(val reason: E) : Result<Nothing, E>() {
        override val isSuccess: Boolean = false
        override val isFailure: Boolean = true
        override val get: Nothing
            get() = throw NoSuchElementException("The result does not contain a value.")
        override val error: E = reason

        override fun toString(): String = "Failure($reason)"
    }
}

infix fun <T, E> T.validate(rule: ValidationRule<T, E>): Result<T, E> = when (val result = rule.test(this)) {
    is ValidationResult.Ok -> Result.success(this)
    is ValidationResult.Fail -> Result.failure(result.error)
}

infix fun <T, E> Result<T, E>.validate(rule: ValidationRule<T, E>): Result<T, E> = when (this) {
    is Result.Success -> {
        val result = rule.test(value)
        if (result.isError)
            Result.failure(result.error)
        else
            Result.success(value)
    }
    is Result.Failure -> this
}

infix fun <T, R, E> Result<T, E>.flatMap(transform: (T) -> Result<R, E>): Result<R, E> = when (this) {
    is Result.Success -> transform(value)
    is Result.Failure -> this
}