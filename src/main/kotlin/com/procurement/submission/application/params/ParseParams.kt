package com.procurement.submission.application.params

import com.procurement.submission.domain.extension.UUID_PATTERN
import com.procurement.submission.domain.extension.tryParseLocalDateTime
import com.procurement.submission.domain.extension.tryUUID
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.fail.error.DataTimeError
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.EnumElementProvider
import com.procurement.submission.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.ProcurementMethodModalities
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.item.ItemId
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.tryOwner
import com.procurement.submission.model.dto.ocds.Amount
import java.math.BigDecimal
import java.time.LocalDateTime

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "cpid",
                pattern = Cpid.pattern,
                actualValue = value
            )
        )

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "ocid",
                pattern = Ocid.pattern,
                actualValue = value
            )
        )

fun parseQualificationStatusDetails(
    value: String, allowedEnums: Set<QualificationStatusDetails>, attributeName: String
): Result<QualificationStatusDetails, DataErrors> =
    parseEnum(
        value = value,
        allowedEnums = allowedEnums,
        attributeName = attributeName,
        target = QualificationStatusDetails
    )

fun parseOperationType(
    value: String,
    allowedEnums: Set<OperationType>,
    attributeName: String = "operationType"
): Result<OperationType, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = OperationType)

fun parsePmd(
    value: String,
    allowedEnums: Set<ProcurementMethod>,
    attributeName: String = "pmd"
): Result<ProcurementMethod, DataErrors.Validation.UnknownValue> =
    ProcurementMethod.orNull(value)
        ?.takeIf { it in allowedEnums }
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.UnknownValue(
                name = attributeName,
                expectedValues = allowedEnums.map { it.name },
                actualValue = value
            )
        )

fun parseDocumentType(
    value: String,
    allowedEnums: Set<DocumentType>,
    attributeName: String
): Result<DocumentType, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = DocumentType)

fun parseTypeOfSupplier(
    value: String,
    allowedEnums: Set<TypeOfSupplier>,
    attributeName: String
): Result<TypeOfSupplier, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = TypeOfSupplier)

fun parseScale(
    value: String,
    allowedEnums: Set<Scale>,
    attributeName: String
): Result<Scale, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = Scale)

fun parsePersonTitle(
    value: String,
    allowedEnums: Set<PersonTitle>,
    attributeName: String
): Result<PersonTitle, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = PersonTitle)

fun parseBusinessFunctionType(
    value: String,
    allowedEnums: Set<BusinessFunctionType>,
    attributeName: String
): Result<BusinessFunctionType, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = BusinessFunctionType)

fun parseProcurementMethodModalities(
    value: String,
    allowedEnums: Set<ProcurementMethodModalities>,
    attributeName: String
): Result<ProcurementMethodModalities, DataErrors> =
    parseEnum(value = value, allowedEnums = allowedEnums, attributeName = attributeName, target = ProcurementMethodModalities)

private fun <T> parseEnum(
    value: String, allowedEnums: Set<T>, attributeName: String, target: EnumElementProvider<T>
): Result<T, DataErrors.Validation.UnknownValue> where T : Enum<T>,
                                                       T : EnumElementProvider.Key =

    target.orNull(value)
        ?.takeIf { it in allowedEnums }
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.UnknownValue(
                name = attributeName,
                expectedValues = allowedEnums.keysAsStrings(),
                actualValue = value
            )
        )

fun parseDate(value: String, attributeName: String = "date"): Result<LocalDateTime, DataErrors.Validation> =
    value.tryParseLocalDateTime()
        .mapError { fail ->
            when (fail) {
                is DataTimeError.InvalidFormat   -> DataErrors.Validation.DataFormatMismatch(
                    name = attributeName,
                    actualValue = value,
                    expectedFormat = fail.pattern
                )

                is DataTimeError.InvalidDateTime ->
                    DataErrors.Validation.InvalidDateTime(name = attributeName, actualValue = value)
            }
        }

fun parseOwner(value: String): Result<Owner, DataErrors.Validation.DataFormatMismatch> =
    value.tryOwner()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "owner",
                    actualValue = value,
                    expectedFormat = "uuid"
                )
            )
        }
        .asSuccess()

fun parseQualificationId(
    value: String, attributeName: String
): Result<QualificationId, DataErrors.Validation.DataMismatchToPattern> {
    val id = QualificationId.tryCreateOrNull(value)
        ?: return DataErrors.Validation.DataMismatchToPattern(
            name = attributeName,
            pattern = QualificationId.pattern,
            actualValue = value
        ).asFailure()

    return id.asSuccess()
}

fun parseBidId(
    value: String, attributeName: String
): Result<BidId, DataErrors.Validation.DataMismatchToPattern> {
    val id = value.tryUUID().doReturn {
        return DataErrors.Validation.DataMismatchToPattern(
            name = attributeName,
            pattern = UUID_PATTERN,
            actualValue = value
        ).asFailure()
    }

    return id.asSuccess()
}

fun parseItemId(
    value: String, attributeName: String
): Result<ItemId, DataErrors.Validation.DataMismatchToPattern> =
    if (!ItemId.validate(value))
        DataErrors.Validation.DataMismatchToPattern(
            name = attributeName,
            pattern = UUID_PATTERN,
            actualValue = value
        ).asFailure()
    else ItemId.create(value).asSuccess()

fun parseAmount(
    value: BigDecimal, attributeName: String
): Result<Amount, DataErrors.Validation.DataMismatchToPattern> {
    val amount = Amount.tryCreate(value).doReturn { error ->
        return DataErrors.Validation.DataMismatchToPattern(
            name = attributeName,
            pattern = error.description,
            actualValue = value.toString()
        ).asFailure()
    }

    return amount.asSuccess()
}