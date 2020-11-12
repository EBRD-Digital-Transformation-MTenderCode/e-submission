package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.bid.ValidateBidDataParams
import com.procurement.submission.application.params.parseAmount
import com.procurement.submission.application.params.parseBFDocumentType
import com.procurement.submission.application.params.parseBidId
import com.procurement.submission.application.params.parseBusinessFunctionType
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseDate
import com.procurement.submission.application.params.parseDocumentType
import com.procurement.submission.application.params.parseItemId
import com.procurement.submission.application.params.parsePersonTitle
import com.procurement.submission.application.params.parseProcurementMethodModalities
import com.procurement.submission.application.params.parseScale
import com.procurement.submission.application.params.parseTypeOfSupplier
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.domain.model.enums.ProcurementMethodModalities
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.infrastructure.handler.v2.model.request.ValidateBidDataRequest
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.flatMap
import com.procurement.submission.lib.functional.validate

fun ValidateBidDataRequest.convert(): Result<ValidateBidDataParams, DataErrors> {
    val path = "bids"

    return ValidateBidDataParams(
        bids = bids.convert(path).onFailure { return it },
        tender = tender.convert(path).onFailure { return it },
        cpid = parseCpid(cpid).onFailure { return it }
    ).asSuccess()
}

private val allowedProcurementMethodModalities = ProcurementMethodModalities.allowedElements
    .filter {
        when (it) {
            ProcurementMethodModalities.ELECTRONIC_AUCTION,
            ProcurementMethodModalities.REQUIRES_ELECTRONIC_CATALOGUE -> true
        }
    }
    .toSet()

private fun ValidateBidDataRequest.Tender.convert(path: String): Result<ValidateBidDataParams.Tender, DataErrors> {
    val procurementMethodModalities =
        procurementMethodModalities.validate(notEmptyRule("$path.procurementMethodModalities"))
            .flatMap {
                it.orEmpty()
                    .mapResult { procurementMethodModality ->
                        parseProcurementMethodModalities(
                            value = procurementMethodModality,
                            allowedEnums = allowedProcurementMethodModalities,
                            attributeName = "$path.procurementMethodModalities"
                        )
                    }
            }
            .onFailure { return it }

    val items = items.validate(notEmptyRule("$path.items"))
        .flatMap { it.orEmpty().mapResult { item -> item.convert("$path.items") } }
        .onFailure { return it }

    return ValidateBidDataParams.Tender(
        procurementMethodModalities = procurementMethodModalities,
        value = ValidateBidDataParams.Tender.Value(currency = this.value.currency),
        items = items

    ).asSuccess()
}

private fun ValidateBidDataRequest.Tender.Item.convert(path: String): Result<ValidateBidDataParams.Tender.Item, DataErrors> {
    val id = parseItemId(id, "$path.item")
        .onFailure { return it }

    val unit = ValidateBidDataParams.Tender.Item.Unit(unit.id)

    return ValidateBidDataParams.Tender.Item(
        id = id,
        unit = unit
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.convert(path: String): Result<ValidateBidDataParams.Bids, DataErrors> {
    val details = details.validate(notEmptyRule("$path.details"))
        .flatMap { it.mapResult { detail -> detail.convert("$path.details") } }
        .onFailure { return it }

    return ValidateBidDataParams.Bids(
        details = details
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.convert(path: String): Result<ValidateBidDataParams.Bids.Detail, DataErrors> {
    val id = parseBidId(id, "$path.id")
        .onFailure { return it }

    val items = items.validate(notEmptyRule("$path.items"))
        .flatMap { it.orEmpty().mapResult { item -> item.convert("$path.items") } }
        .onFailure { return it }

    val relatedLots = relatedLots.validate(notEmptyRule("$path.relatedLots"))
        .onFailure { return it }

    val value = value?.convert("$path.value")
        ?.onFailure { return it }

    val documents = documents.validate(notEmptyRule("$path.documents"))
        .flatMap { it.orEmpty().mapResult { document -> document.convert("$path.documents") } }
        .onFailure { return it }

    val tenderers = tenderers.validate(notEmptyRule("$path.tenderers"))
        .flatMap { it.mapResult { tenderer -> tenderer.convert("$path.tenderers") } }
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail(
        id = id,
        items = items,
        relatedLots = relatedLots,
        value = value,
        documents = documents,
        tenderers = tenderers
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer, DataErrors> {
    val additionalIdentifiers = additionalIdentifiers.validate(notEmptyRule("$path.additionalIdentifiers"))
        .onFailure { return it }
        .orEmpty()
        .map { additionalIdentifier -> additionalIdentifier.convert() }

    val address = address.convert()
    val contactPoint = contactPoint.convert()
    val details = details.convert("$path.details").onFailure { return it }
    val identifier = identifier.convert()

    val persones = persones.validate(notEmptyRule("$path.persones"))
        .flatMap { it.orEmpty().mapResult { person -> person.convert("$path.persones") } }
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer(
        id = id,
        additionalIdentifiers = additionalIdentifiers,
        address = address,
        contactPoint = contactPoint,
        details = details,
        identifier = identifier,
        name = name,
        persones = persones
    ).asSuccess()
}

private val allowedPersonTitles = PersonTitle.allowedElements
    .filter {
        when (it) {
            PersonTitle.MR,
            PersonTitle.MRS,
            PersonTitle.MS -> true
        }
    }
    .toSet()

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Persone, DataErrors> {

    val title = parsePersonTitle(title, allowedPersonTitles, "$path.title")
        .onFailure { return it }

    val identifier = identifier.convert()

    val businessFunctions = businessFunctions.validate(notEmptyRule("$path.businessFunctions"))
        .flatMap { it.mapResult { businessFunction -> businessFunction.convert("$path.businessFunctions") } }
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Persone(
        id = id,
        title = title,
        name = name,
        identifier = identifier,
        businessFunctions = businessFunctions
    ).asSuccess()
}

private val allowedBusinessFunctionType = BusinessFunctionType.allowedElements
    .filter {
        when (it) {
            BusinessFunctionType.AUTHORITY,
            BusinessFunctionType.CONTACT_POINT -> true
        }
    }.toSet()

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.BusinessFunction.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction, DataErrors> {
    val documents = documents.validate(notEmptyRule("$path.documents"))
        .flatMap { it.orEmpty().mapResult { document -> document.convert("$path.documents") } }
        .onFailure { return it }

    val type = parseBusinessFunctionType(type, allowedBusinessFunctionType, "$path.type")
        .onFailure { return it }

    val period = period.convert("$path.period").onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction(
        id = id,
        documents = documents,
        jobTitle = jobTitle,
        type = type,
        period = period
    ).asSuccess()
}

private val allowedBFDocumentType = BusinessFunctionDocumentType.allowedElements
    .filter {
        when (it) {
            BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> true
        }
    }
    .toSet()

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.BusinessFunction.Document.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction.Document, DataErrors> {
    val documentType = parseBFDocumentType(documentType, allowedBFDocumentType, "$path.documentType")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction.Document(
        id = id,
        description = description,
        title = title,
        documentType = documentType
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.BusinessFunction.Period.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction.Period, DataErrors> {
    val startDate = parseDate(startDate, "$path.startDate")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Persone.BusinessFunction.Period(
        startDate = startDate
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.Identifier.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Persone.Identifier(
    id = id,
    uri = uri,
    scheme = scheme
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Identifier.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Identifier(
    id = id,
    scheme = scheme,
    uri = uri,
    legalName = legalName
)

private val allowedTypeOfSupplier = TypeOfSupplier.allowedElements
    .filter {
        when (it) {
            TypeOfSupplier.COMPANY,
            TypeOfSupplier.INDIVIDUAL -> true
        }
    }
    .toSet()

private val allowedScales = Scale.allowedElements
    .filter {
        when (it) {
            Scale.SME,
            Scale.LARGE,
            Scale.MICRO -> true
            Scale.EMPTY -> false
        }
    }
    .toSet()

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details, DataErrors> {

    val typeOfSupplier = typeOfSupplier?.let {
        parseTypeOfSupplier(
            it, allowedTypeOfSupplier, "$path.typeOfSupplier"
        ).onFailure { return it }
    }

    val bankAccounts = bankAccounts.validate(notEmptyRule("$path.bankAccounts"))
        .flatMap { it.orEmpty().mapResult { bankAccount -> bankAccount.convert("$path.bankAccounts") } }
        .onFailure { return it }

    val legalForm = legalForm?.convert()

    val mainEconomicActivities = mainEconomicActivities.validate(notEmptyRule("$path.mainEconomicActivities"))
        .onFailure { return it }
        .orEmpty()
        .map { mainEconomicActivity -> mainEconomicActivity.convert() }

    val permits = permits.validate(notEmptyRule("$path.permits"))
        .flatMap { it.orEmpty().mapResult { permit -> permit.convert("$path.permits") } }
        .onFailure { return it }

    val scale = parseScale(scale, allowedScales, "$path.scale")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details(
        typeOfSupplier = typeOfSupplier,
        bankAccounts = bankAccounts,
        legalForm = legalForm,
        mainEconomicActivities = mainEconomicActivities,
        permits = permits,
        scale = scale
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit, DataErrors> {
    val permitDetails = permitDetails.convert("$path.permitDetails")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit(
        id = id,
        scheme = scheme,
        url = url,
        permitDetails = permitDetails
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails, DataErrors> {
    val issuedBy = issuedBy.convert()
    val issuedThought = issuedThought.convert()
    val validityPeriod = validityPeriod.convert("$path.validityPeriod")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails(
        issuedBy = issuedBy,
        issuedThought = issuedThought,
        validityPeriod = validityPeriod
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.ValidityPeriod.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.ValidityPeriod, DataErrors> {
    val startDate = parseDate(value = startDate, attributeName = "$path.startDate")
        .onFailure { return it }

    val endDate = endDate?.let {
        parseDate(value = it, attributeName = "$path.endDate")
            .onFailure { return it }
    }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought(
    id = id,
    name = name
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy(
    id = id,
    name = name
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount, DataErrors> {
    val identifier = identifier.convert()
    val address = address.convert()
    val accountIdentification = accountIdentification.convert()

    val additionalAccountIdentifiers =
        additionalAccountIdentifiers.validate(notEmptyRule("$path.additionalAccountIdentifiers"))
            .onFailure { return it }
        .orEmpty()
        .map { bankAccount -> bankAccount.convert() }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount(
        description = description,
        bankName = bankName,
        identifier = identifier,
        address = address,
        accountIdentification = accountIdentification,
        additionalAccountIdentifiers = additionalAccountIdentifiers
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
    id = id,
    scheme = scheme
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification(
    id = id,
    scheme = scheme
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.Address.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address(
    streetAddress = streetAddress,
    postalCode = postalCode,
    addressDetails = ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails(
        country = addressDetails.country.let { country ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                id = country.id,
                description = country.description,
                scheme = country.scheme
            )
        },
        locality = addressDetails.locality.let { locality ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                id = locality.id,
                description = locality.description,
                scheme = locality.scheme
            )
        },
        region = addressDetails.region.let { region ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                id = region.id,
                description = region.description,
                scheme = region.scheme
            )
        }
    )
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.Identifier.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Identifier(
    id = id,
    scheme = scheme
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.MainEconomicActivity.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.MainEconomicActivity(
    id = id,
    scheme = scheme,
    uri = uri,
    description = description
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.LegalForm.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Details.LegalForm(
    id = id,
    scheme = scheme,
    uri = uri,
    description = description
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.ContactPoint.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.ContactPoint(
    name = name,
    email = email,
    faxNumber = faxNumber,
    telephone = telephone,
    url = url
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Address.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.Address(
    streetAddress = streetAddress,
    postalCode = postalCode,
    addressDetails = ValidateBidDataParams.Bids.Detail.Tenderer.Address.AddressDetails(
        country = addressDetails.country.let { country ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Address.AddressDetails.Country(
                id = country.id,
                description = country.description,
                scheme = country.scheme
            )
        },
        locality = addressDetails.locality.let { locality ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Address.AddressDetails.Locality(
                id = locality.id,
                description = locality.description,
                scheme = locality.scheme
            )
        },
        region = addressDetails.region.let { region ->
            ValidateBidDataParams.Bids.Detail.Tenderer.Address.AddressDetails.Region(
                id = region.id,
                description = region.description,
                scheme = region.scheme
            )
        }
    )
)

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.AdditionalIdentifier.convert() = ValidateBidDataParams.Bids.Detail.Tenderer.AdditionalIdentifier(
    id = id,
    scheme = scheme,
    legalName = legalName,
    uri = uri
)

private val allowedBidDocumentType = DocumentType.allowedElements
    .filter {
        when (it) {
            DocumentType.COMMERCIAL_OFFER,
            DocumentType.ELIGIBILITY_DOCUMENTS,
            DocumentType.ILLUSTRATION,
            DocumentType.QUALIFICATION_DOCUMENTS,
            DocumentType.SUBMISSION_DOCUMENTS,
            DocumentType.TECHNICAL_DOCUMENTS -> true
        }
    }.toSet()

private fun ValidateBidDataRequest.Bids.Detail.Document.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Document, DataErrors> {
    val relatedLots = relatedLots.validate(notEmptyRule("$path.relatedLots"))
        .onFailure { return it }
        .orEmpty()

    val documentType = parseDocumentType(documentType, allowedBidDocumentType, "$path.documentType")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Document(
        id = id,
        description = description,
        relatedLots = relatedLots,
        documentType = documentType,
        title = title
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Value.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Value, DataErrors> {
    val amount = parseAmount(amount, "$path.amount")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Value(
        currency = currency,
        amount = amount
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Item.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Item, DataErrors> {

    val id = parseItemId(id, "$path.id")
        .onFailure { return it }

    val unit = unit.convert("$path.unit")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Item(
        id = id,
        unit = unit
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Item.Unit.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Item.Unit, DataErrors> {
    val value = value.convert("$path.value")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Item.Unit(
        id = id,
        value = value
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Item.Unit.Value.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Item.Unit.Value, DataErrors> {
    val amount = parseAmount(amount, "$path.value")
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Item.Unit.Value(
        amount = amount,
        currency = currency
    ).asSuccess()
}

