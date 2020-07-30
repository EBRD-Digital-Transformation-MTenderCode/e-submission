package com.procurement.submission.domain.fail.error

import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.fail.Fail

sealed class DataErrors(numberError: String, override val description: String) : Fail.Error("DR-") {

    override val code: String = prefix + numberError

    sealed class Validation(numberError: String, val name: String, description: String) :
        DataErrors(numberError = numberError, description = description) {

        companion object {
            const val ATTRIBUTE_NAME_KEY = "attributeName"
        }

        override fun logging(logger: Logger) {
            logger.error(message = message, mdc = mapOf(ATTRIBUTE_NAME_KEY to name))
        }

        class MissingRequiredAttribute(name: String) :
            Validation(numberError = "1", description = "Missing required attribute '$name'.", name = name)

        class DataTypeMismatch(name: String, expectedType: String, actualType: String) : Validation(
            numberError = "2",
            description = "Data type mismatch of attribute '$name'. Expected data type: '$expectedType', actual data type: '$actualType'.",
            name = name
        )

        class UnknownValue(name: String, expectedValues: Collection<String>, actualValue: String) : Validation(
            numberError = "3",
            description = "Attribute value mismatch of '$name' with one of enum expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'.",
            name = name
        )

        class DataFormatMismatch(name: String, expectedFormat: String, actualValue: String) : Validation(
            numberError = "4",
            description = "Data format mismatch of attribute '$name'. Expected data format: '$expectedFormat', actual value: '$actualValue'.",
            name = name
        )

        class DataMismatchToPattern(name: String, pattern: String, actualValue: String) : Validation(
            numberError = "5",
            description = "Data mismatch of attribute '$name' to the pattern: '$pattern'. Actual value: '$actualValue'.",
            name = name
        )

        class UniquenessDataMismatch(name: String, value: String) :
            Validation(
                numberError = "6",
                description = "Uniqueness data mismatch of attribute '$name': '$value'.",
                name = name
            )

        class InvalidNumberOfElementsInArray(name: String, min: Int? = null, max: Int? = null, actualLength: Int) :
            Validation(
                numberError = "7",
                description = "Invalid number of objects in the array of attribute '$name'. Expected length from '${min ?: "none min"}' to '${max ?: "none max"}', actual length: '$actualLength'.",
                name = name
            )

        class InvalidStringLength(name: String, min: Int? = null, max: Int? = null, actualLength: Int) : Validation(
            numberError = "8",
            description = "Invalid number of chars in string of attribute '$name'. Expected length from '${min ?: "none min"}' to '${max ?: "none max"}', actual length: '$actualLength'.",
            name = name
        )

        class EmptyObject(name: String) :
            Validation(numberError = "9", description = "Attribute '$name' is an empty object.", name = name)

        class EmptyArray(name: String) :
            Validation(numberError = "10", description = "Attribute '$name' is an empty array.", name = name)

        class EmptyString(name: String) :
            Validation(numberError = "11", description = "Attribute '$name' is an empty string.", name = name)

        class UnexpectedAttribute(name: String) :
            Validation(numberError = "12", description = "Unexpected attribute '$name'.", name = name)

        class InvalidDateTime(name: String, actualValue: String) :
            Validation(numberError = "13", description = "Invalid date-time: '$actualValue'.", name = name)
    }
}