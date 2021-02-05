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
import com.procurement.submission.application.params.parsePmd
import com.procurement.submission.application.params.parseProcurementMethodModalities
import com.procurement.submission.application.params.parseScale
import com.procurement.submission.application.params.parseTypeOfSupplier
import com.procurement.submission.application.params.rules.notEmptyOrBlankRule
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.domain.model.enums.ProcurementMethod
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
        cpid = parseCpid(cpid).onFailure { return it },
        pmd = parsePmd(pmd, allowedPmd).onFailure { return it },
        mdm = ValidateBidDataParams.Mdm(
            registrationSchemes = mdm.registrationSchemes.map { registrationScheme ->
                ValidateBidDataParams.Mdm.RegistrationScheme(
                    country = registrationScheme.country,
                    schemes = registrationScheme.schemes
                )
            }
        )
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

private val allowedPmd = ProcurementMethod.values()
    .filter {
        when (it) {
            ProcurementMethod.CF, ProcurementMethod.TEST_CF,
            ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
            ProcurementMethod.MV, ProcurementMethod.TEST_MV,
            ProcurementMethod.OF, ProcurementMethod.TEST_OF,
            ProcurementMethod.OT, ProcurementMethod.TEST_OT,
            ProcurementMethod.RT, ProcurementMethod.TEST_RT,
            ProcurementMethod.SV, ProcurementMethod.TEST_SV -> true

            ProcurementMethod.CD, ProcurementMethod.TEST_CD,
            ProcurementMethod.DA, ProcurementMethod.TEST_DA,
            ProcurementMethod.DC, ProcurementMethod.TEST_DC,
            ProcurementMethod.FA, ProcurementMethod.TEST_FA,
            ProcurementMethod.IP, ProcurementMethod.TEST_IP,
            ProcurementMethod.NP, ProcurementMethod.TEST_NP,
            ProcurementMethod.OP, ProcurementMethod.TEST_OP -> false
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
        .flatMap { it.orEmpty().mapResult { document -> document.convert("$path.documents[${document.id}]") } }
        .onFailure { return it }

    val tenderers = tenderers.validate(notEmptyRule("$path.tenderers"))
        .flatMap { it.mapResult { tenderer -> tenderer.convert("$path.tenderers[${tenderer.id}]") } }
        .onFailure { return it }

    val requirementResponses = requirementResponses.validate(notEmptyRule("$path.requirementResponses"))
        .flatMap { it.orEmpty().mapResult { requirementResponse -> requirementResponse.convert("$path.requirementResponses[${requirementResponse.id}]") } }
        .onFailure { return it }

    return ValidateBidDataParams.Bids.Detail(
        id = id,
        items = items,
        relatedLots = relatedLots,
        value = value,
        documents = documents,
        tenderers = tenderers,
        requirementResponses = requirementResponses
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer, DataErrors> {
    val additionalIdentifiers = additionalIdentifiers.validate(notEmptyRule("$path.additionalIdentifiers"))
        .onFailure { return it }
        .orEmpty()
        .map { additionalIdentifier -> additionalIdentifier.convert("$path.additionalIdentifiers[${additionalIdentifier.id}]").onFailure { return it } }

    val name = name.validate(notEmptyOrBlankRule("$path.name")).onFailure { return it }

    val address = address.convert("$path.address").onFailure { return it }
    val contactPoint = contactPoint.convert("$path.contactPoint").onFailure { return it }
    val details = details.convert("$path.details").onFailure { return it }
    val identifier = identifier.convert("$path.identifier").onFailure { return it }

    val persones = persones.validate(notEmptyRule("$path.persones"))
        .flatMap { it.orEmpty().mapResult { person -> person.convert("$path.persones[${person.id}]") } }
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

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.RequirementResponse, DataErrors> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }

    val requirement = requirement.convert()
    val relatedTenderer = relatedTenderer?.convert()
    val period = period?.convert()

    val evidences = evidences.validate(notEmptyRule("$path.evidences"))
        .onFailure { return it }
        .orEmpty()
        .map { it.convert() }

    return ValidateBidDataParams.Bids.Detail.RequirementResponse(
        id = id,
        value = value,
        requirement = requirement,
        relatedTenderer = relatedTenderer,
        evidences = evidences,
        period = period
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.Requirement.convert(): ValidateBidDataParams.Bids.Detail.RequirementResponse.Requirement =
    ValidateBidDataParams.Bids.Detail.RequirementResponse.Requirement(id = id)

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.OrganizationReference.convert(): ValidateBidDataParams.Bids.Detail.RequirementResponse.OrganizationReference =
    ValidateBidDataParams.Bids.Detail.RequirementResponse.OrganizationReference(
        id = id,
        name = name
    )

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.Evidence.convert(): ValidateBidDataParams.Bids.Detail.RequirementResponse.Evidence =
    ValidateBidDataParams.Bids.Detail.RequirementResponse.Evidence(
        id = id,
        title = title,
        description = description,
        relatedDocument = relatedDocument?.convert()
    )

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.Evidence.RelatedDocument.convert(): ValidateBidDataParams.Bids.Detail.RequirementResponse.Evidence.RelatedDocument =
    ValidateBidDataParams.Bids.Detail.RequirementResponse.Evidence.RelatedDocument(id = id)

private fun ValidateBidDataRequest.Bids.Detail.RequirementResponse.Period.convert(): ValidateBidDataParams.Bids.Detail.RequirementResponse.Period =
    ValidateBidDataParams.Bids.Detail.RequirementResponse.Period(
        startDate = startDate,
        endDate = endDate
    )

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
    val name = name.validate(notEmptyOrBlankRule("$path.name")).onFailure { return it }

    val title = parsePersonTitle(title, allowedPersonTitles, "$path.title")
        .onFailure { return it }

    val identifier = identifier.convert("$path.identifier").onFailure { return it }

    val businessFunctions = businessFunctions.validate(notEmptyRule("$path.businessFunctions"))
        .flatMap { it.mapResult { businessFunction -> businessFunction.convert("$path.businessFunctions[${businessFunction.id}]") } }
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
    val jobTitle = jobTitle.validate(notEmptyOrBlankRule("$path.jobTitle")).onFailure { return it }

    val documents = documents.validate(notEmptyRule("$path.documents"))
        .flatMap { it.orEmpty().mapResult { document -> document.convert("$path.documents[${document.id}]") } }
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
    val description = description?.validate(notEmptyOrBlankRule("$path.description"))?.onFailure { return it }

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

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Persone.Identifier.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Persone.Identifier, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val uri = uri?.validate(notEmptyOrBlankRule("$path.uri"))?.onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Persone.Identifier(
        id = id,
        uri = uri,
        scheme = scheme
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Identifier.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Identifier, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }
    val uri = uri?.validate(notEmptyOrBlankRule("$path.uri"))?.onFailure { return it }
    val legalName = legalName.validate(notEmptyOrBlankRule("$path.legalName")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Identifier(
        id = id,
        scheme = scheme,
        uri = uri,
        legalName = legalName
    ).asSuccess()
}

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
        .flatMap { it.orEmpty().mapResult { bankAccount -> bankAccount.convert("$path.bankAccounts[${bankAccount.identifier.scheme}-${bankAccount.identifier.id}]") } }
        .onFailure { return it }

    val legalForm = legalForm?.convert("$path.legalForm")?.onFailure { return it }

    val mainEconomicActivities = mainEconomicActivities.validate(notEmptyRule("$path.mainEconomicActivities"))
        .onFailure { return it }
        .orEmpty()
        .map { mainEconomicActivity -> mainEconomicActivity.convert("$path.mainEconomicActivities[${mainEconomicActivity.id}]").onFailure { return it } }

    val permits = permits.validate(notEmptyRule("$path.permits"))
        .flatMap { it.orEmpty().mapResult { permit -> permit.convert("$path.permits[${permit.id}]") } }
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
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }
    val url = url?.validate(notEmptyOrBlankRule("$path.url"))?.onFailure { return it }

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
    val issuedBy = issuedBy.convert("$path.issuedBy").onFailure { return it }
    val issuedThought = issuedThought.convert("$path.issuedThought").onFailure { return it }
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

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val name = name.validate(notEmptyOrBlankRule("$path.name")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedThought(
        id = id,
        name = name
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val name = name.validate(notEmptyOrBlankRule("$path.name")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.Permit.PermitDetails.IssuedBy(
        id = id,
        name = name
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount, DataErrors> {
    val description = description.validate(notEmptyOrBlankRule("$path.description")).onFailure { return it }
    val bankName = bankName.validate(notEmptyOrBlankRule("$path.bankName")).onFailure { return it }

    val identifier = identifier.convert("$path.identifier").onFailure { return it }
    val address = address.convert("$path.address").onFailure { return it }
    val accountIdentification = accountIdentification.convert("$path.accountIdentification").onFailure { return it }

    val additionalAccountIdentifiers =
        additionalAccountIdentifiers.validate(notEmptyRule("$path.additionalAccountIdentifiers"))
            .onFailure { return it }
            .orEmpty()
            .map { bankAccount -> bankAccount.convert("$path.additionalAccountIdentifiers[${bankAccount.scheme}-${bankAccount.id}]").onFailure { return it } }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount(
        description = description,
        bankName = bankName,
        identifier = identifier,
        address = address,
        accountIdentification = accountIdentification,
        additionalAccountIdentifiers = additionalAccountIdentifiers
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier, DataErrors>{
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
        id = id,
        scheme = scheme
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.AccountIdentification(
        id = id,
        scheme = scheme
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.Address.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address, DataErrors.Validation> {
    val streetAddress = streetAddress.validate(notEmptyOrBlankRule("$path.streetAddress")).onFailure { return it }
    val postalCode = postalCode?.validate(notEmptyOrBlankRule("$path.postalCode"))?.onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address(
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
                val description = locality.description.validate(notEmptyOrBlankRule("$path.addressDetails.locality.description")).onFailure { return it }
                ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                    id = locality.id,
                    description = description,
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
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.BankAccount.Identifier.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Identifier, DataErrors> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.BankAccount.Identifier(
        id = id,
        scheme = scheme
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.MainEconomicActivity.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.MainEconomicActivity, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }
    val uri = uri?.validate(notEmptyOrBlankRule("$path.uri"))?.onFailure { return it }
    val description = description.validate(notEmptyOrBlankRule("$path.description")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.MainEconomicActivity(
        id = id,
        scheme = scheme,
        uri = uri,
        description = description
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Details.LegalForm.convert(path: String) : Result<ValidateBidDataParams.Bids.Detail.Tenderer.Details.LegalForm, DataErrors> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }
    val uri = uri?.validate(notEmptyOrBlankRule("$path.uri"))?.onFailure { return it }
    val description = description.validate(notEmptyOrBlankRule("$path.description")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Details.LegalForm(
        id = id,
        scheme = scheme,
        uri = uri,
        description = description
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.ContactPoint.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.ContactPoint, DataErrors.Validation> {
    val name = name.validate(notEmptyOrBlankRule("$path.name")).onFailure { return it }
    val email = email.validate(notEmptyOrBlankRule("$path.email")).onFailure { return it }
    val faxNumber = faxNumber?.validate(notEmptyOrBlankRule("$path.faxNumber"))?.onFailure { return it }
    val telephone = telephone.validate(notEmptyOrBlankRule("$path.telephone")).onFailure { return it }
    val url = url?.validate(notEmptyOrBlankRule("$path.url"))?.onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.ContactPoint(
        name = name,
        email = email,
        faxNumber = faxNumber,
        telephone = telephone,
        url = url
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.Address.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.Address, DataErrors.Validation> {
    val streetAddress = streetAddress.validate(notEmptyOrBlankRule("$path.streetAddress")).onFailure { return it }
    val postalCode = postalCode?.validate(notEmptyOrBlankRule("$path.postalCode"))?.onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.Address(
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
                val description = locality.description.validate(notEmptyOrBlankRule("$path.addressDetails.locality.description")).onFailure { return it }
                val scheme = locality.scheme.validate(notEmptyOrBlankRule("$path.addressDetails.locality.scheme")).onFailure { return it }

                ValidateBidDataParams.Bids.Detail.Tenderer.Address.AddressDetails.Locality(
                    id = locality.id,
                    description = description,
                    scheme = scheme
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
    ).asSuccess()
}

private fun ValidateBidDataRequest.Bids.Detail.Tenderer.AdditionalIdentifier.convert(path: String): Result<ValidateBidDataParams.Bids.Detail.Tenderer.AdditionalIdentifier, DataErrors.Validation> {
    val id = id.validate(notEmptyOrBlankRule("$path.id")).onFailure { return it }
    val scheme = scheme.validate(notEmptyOrBlankRule("$path.scheme")).onFailure { return it }
    val uri = uri?.validate(notEmptyOrBlankRule("$path.uri"))?.onFailure { return it }
    val legalName = legalName.validate(notEmptyOrBlankRule("$path.legalName")).onFailure { return it }

    return ValidateBidDataParams.Bids.Detail.Tenderer.AdditionalIdentifier(
        id = id,
        scheme = scheme,
        legalName = legalName,
        uri = uri
    ).asSuccess()
}

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
    val title = title.validate(notEmptyOrBlankRule("$path.title")).onFailure { return it }
    val description = description?.validate(notEmptyOrBlankRule("$path.description"))?.onFailure { return it }

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

