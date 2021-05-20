package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.PersonesProcessingParams
import com.procurement.submission.application.params.parseBFDocumentType
import com.procurement.submission.application.params.parseBusinessFunctionType
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseDate
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.parseParsePersonId
import com.procurement.submission.application.params.parsePersonTitle
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.infrastructure.exception.EmptyStringException
import com.procurement.submission.infrastructure.handler.v2.model.request.PersonesProcessingRequest
import com.procurement.submission.lib.errorIfBlank
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.model.dto.ocds.PersonId

fun PersonesProcessingRequest.convert(): Result<PersonesProcessingParams, Fail> {
    validateTextAttributes(parties = parties)
        .onFailure { return it.reason.asFailure() }

    val parties = parties
        .mapResult { it.convert() }
        .onFailure { return it }

    return PersonesProcessingParams(
        cpid = parseCpid(cpid).onFailure { return it },
        ocid = parseOcid(ocid).onFailure { return it },
        parties = parties
    ).asSuccess()
}

private fun PersonesProcessingRequest.Party.convert(): Result<PersonesProcessingParams.Party, Fail> {
    val persones = persones
        .mapResult { it.convert() }
        .onFailure { return it }

    return PersonesProcessingParams.Party(
        id = id,
        persones = persones
    ).asSuccess()
}

private val parsedTitle = PersonTitle.allowedElements.toSet()

private fun PersonesProcessingRequest.Party.Person.convert(): Result<PersonesProcessingParams.Party.Persone, Fail> {
    val parsedId = parseParsePersonId(id, "parties.person.id")
        .onFailure { return it }
    val titleCurrent = parsePersonTitle(title, parsedTitle, "parties.person.title")
        .onFailure { return it }
    val identifier = identifier.let { identifier ->
        PersonesProcessingParams.Party.Persone.Identifier(
            id = identifier.id,
            scheme = identifier.scheme,
            uri = identifier.uri
        )
    }
    val businessFunction = businessFunctions
        .mapResult { it.convert() }
        .onFailure { return it }

    return PersonesProcessingParams.Party.Persone(
        id = parsedId,
        title = titleCurrent,
        name = name,
        identifier = identifier,
        businessFunctions = businessFunction
    ).asSuccess()
}

private val allowedBusinessFunctionType = BusinessFunctionType.allowedElements
    .filter {
        when (it) {
            BusinessFunctionType.AUTHORITY,
            BusinessFunctionType.CONTACT_POINT -> true
        }
    }
    .toSet()

private fun PersonesProcessingRequest.Party.Person.BusinessFunction.convert(): Result<PersonesProcessingParams.Party.Persone.BusinessFunction, Fail> {

    val documents = documents
        ?.mapResult { it.convert() }
        ?.onFailure { return it }

    val businessFunctionType =
        parseBusinessFunctionType(type, allowedBusinessFunctionType, "parties.persones.businessFunctions.type")
            .onFailure { return it }

    val date = parseDate(period.startDate, "parties.persones.businessFunctions.period.startDate")
        .onFailure { return it }
    val period = PersonesProcessingParams.Party.Persone.BusinessFunction.Period(startDate = date)

    return PersonesProcessingParams.Party.Persone.BusinessFunction(
        id = id,
        type = businessFunctionType,
        jobTitle = jobTitle,
        period = period,
        documents = documents ?: emptyList()
    ).asSuccess()
}

private val allowedBFDocumentType = BusinessFunctionDocumentType.allowedElements
    .filter {
        when (it) {
            BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> true
        }
    }
    .toSet()

private fun PersonesProcessingRequest.Party.Person.BusinessFunction.Document.convert(): Result<PersonesProcessingParams.Party.Persone.BusinessFunction.Document, Fail> {
    val documentType = parseBFDocumentType(
        documentType,
        allowedBFDocumentType,
        "parties.persones.businessFunctions.documents.documentType"
    )
        .onFailure { return it }

    return PersonesProcessingParams.Party.Persone.BusinessFunction.Document(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    ).asSuccess()
}

fun validateTextAttributes(parties: List<PersonesProcessingRequest.Party>): Validated<DataErrors.Validation.EmptyString> {
    try {
        parties.forEachIndexed { partyIndex, party ->
            party.apply {
                id.checkForBlank("parties[$partyIndex].id")
                persones.forEachIndexed { personeIndex, persone ->
                    persone.apply {
                        name.checkForBlank("parties[$partyIndex].persones[$personeIndex].name")
                        identifier.apply {
                            id.checkForBlank("parties[$partyIndex].persones[$personeIndex].identifier.id")
                            scheme.checkForBlank("parties[$partyIndex].persones[$personeIndex].identifier.scheme")
                        }
                        businessFunctions.forEachIndexed { businessFunctionIndex, businessFunction ->
                            businessFunction.apply {
                                id.checkForBlank("parties[$partyIndex].persones[$personeIndex].businessFunctions[$businessFunctionIndex].id")
                                jobTitle.checkForBlank("parties[$partyIndex].persones[$personeIndex].businessFunctions[$businessFunctionIndex].jobTitle")
                            }
                        }
                    }
                }
            }
        }
        return Validated.ok()
    } catch (expected: EmptyStringException) {
        return DataErrors.Validation.EmptyString(expected.path).asValidationError()
    }
}

private fun String?.checkForBlank(path: String) = this.errorIfBlank { EmptyStringException(path) }
