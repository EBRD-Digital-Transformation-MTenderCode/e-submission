package com.procurement.submission.domain.fail

import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.model.enums.EnumElementProvider
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated

sealed class Fail {

    abstract val code: String
    abstract val description: String
    val message: String
        get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

    abstract fun logging(logger: Logger)

    abstract class Error(val prefix: String) : Fail() {
        companion object {
            fun <T, E : Error> E.toResult(): Result<T, E> = Result.failure(this)
            fun <E : Error> E.toValidationResult(): Validated<E> = Validated.error(this)
        }

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }
    }

    sealed class Incident(val level: Level, number: String, override val description: String) : Fail() {
        override val code: String = "INC-$number"

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message)
                Level.WARNING -> logger.warn(message)
                Level.INFO -> logger.info(message)
            }
        }

        sealed class Database(val number: String, override val description: String) :
            Incident(level = Level.ERROR, number = number, description = description) {

            abstract val exception: Exception

            class Interaction(override val exception: Exception) :
                Database(number = "1.1", description = "Database incident.") {

                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }

            class Parsing(val column: String, val value: String, override val exception: Exception) :
                Database(
                    number = "1.4",
                    description = "Could not parse data stored in database."
                ) {

                override fun logging(logger: Logger) {
                    logger.error(
                        message = message,
                        mdc = mapOf("column" to column, "value" to value),
                        exception = exception
                    )
                }
            }

            class DatabaseParsing(override val exception: Exception) : Database(
                number = "1.5",
                description = "Internal Server Error.",
            ) {

                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }
        }

        sealed class Transform(val number: String, override val description: String) :
            Incident(level = Level.ERROR, number = number, description = description) {

            abstract val exception: Exception?

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }

            class Parsing(className: String, override val exception: Exception) :
                Transform(number = "2.2", description = "Error parsing to $className.")

            class Mapping(description: String, override val exception: Exception? = null) :
                Transform(number = "2.4", description = description)

            class Deserialization(description: String, override val exception: Exception) :
                Transform(number = "2.5", description = description)

            class Serialization(description: String, override val exception: Exception) :
                Transform(number = "2.6", description = description)
        }

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}




