package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.enums.PartyRole
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.model.dto.ocds.AccountIdentification
import com.procurement.submission.model.dto.ocds.AdditionalAccountIdentifier
import com.procurement.submission.model.dto.ocds.Address
import com.procurement.submission.model.dto.ocds.BankAccount
import com.procurement.submission.model.dto.ocds.BusinessFunction
import com.procurement.submission.model.dto.ocds.ContactPoint
import com.procurement.submission.model.dto.ocds.Details
import com.procurement.submission.model.dto.ocds.Identifier
import com.procurement.submission.model.dto.ocds.IssuedBy
import com.procurement.submission.model.dto.ocds.IssuedThought
import com.procurement.submission.model.dto.ocds.LegalForm
import com.procurement.submission.model.dto.ocds.MainEconomicActivity
import com.procurement.submission.model.dto.ocds.Organization
import com.procurement.submission.model.dto.ocds.Permit
import com.procurement.submission.model.dto.ocds.PermitDetails
import com.procurement.submission.model.dto.ocds.Persone
import com.procurement.submission.model.dto.ocds.ValidityPeriod
import com.procurement.submission.application.params.bid.query.get.GetOrganizationsByReferencesFromPacsParams as Params
import com.procurement.submission.infrastructure.handler.v2.model.request.GetOrganizationsByReferencesFromPacsRequest as Request
import com.procurement.submission.infrastructure.handler.v2.model.response.GetOrganizationsByReferencesFromPacsResult as Response

fun Request.convert(): Result<Params, DataErrors> =
    Params.tryCreate(cpid = cpid, ocid = ocid, parties = parties.map { it.convert() })

fun Request.Party.convert(): Params.Party =
    Params.Party(id = id)

fun Response.Party.Companion.fromDomain(party: Organization, role: PartyRole): Response.Party {
    return Response.Party(
        id = party.id!!,
        name = party.name,
        identifier = Response.Party.Identifier.fromDomain(party.identifier),
        additionalIdentifiers = party.additionalIdentifiers
            ?.map { Response.Party.Identifier.fromDomain(it) }
        ,
        address = Response.Party.Address.fromDomain(party.address),
        contactPoint = Response.Party.ContactPoint.fromDomain(party.contactPoint),
        persons = party.persones
            ?.map { Response.Party.Person.fromDomain(it) }
        ,
        details = Response.Party.Details.fromDomain(party.details),
        roles = listOf(role.key)
    )
}

fun Response.Party.Identifier.Companion.fromDomain(identifier: Identifier): Response.Party.Identifier {
    return Response.Party.Identifier(
        id = identifier.id,
        legalName = identifier.legalName,
        scheme = identifier.scheme,
        uri = identifier.uri
    )
}

fun Response.Party.Address.Companion.fromDomain(address: Address) =
    Response.Party.Address(
        streetAddress = address.streetAddress,
        postalCode = address.postalCode,
        addressDetails = address.addressDetails.let { addressDetails ->
            Response.Party.Address.AddressDetails(
                country = addressDetails.country.let { country ->
                    Response.Party.Address.AddressDetails.Country(
                        id = country.id,
                        scheme = country.scheme,
                        description = country.description,
                        uri = country.uri
                    )
                },
                region = addressDetails.region.let { region ->
                    Response.Party.Address.AddressDetails.Region(
                        id = region.id,
                        scheme = region.scheme,
                        description = region.description,
                        uri = region.uri
                    )
                },
                locality = addressDetails.locality.let { locality ->
                    Response.Party.Address.AddressDetails.Locality(
                        id = locality.id,
                        scheme = locality.scheme,
                        description = locality.description,
                        uri = locality.uri
                    )
                }
            )
        }
    )

fun Response.Party.ContactPoint.Companion.fromDomain(contactPoint: ContactPoint) =
    Response.Party.ContactPoint(
        name = contactPoint.name,
        email = contactPoint.email!!,
        telephone = contactPoint.telephone,
        faxNumber = contactPoint.faxNumber,
        url = contactPoint.url
    )

fun Response.Party.Person.Companion.fromDomain(persone: Persone) =
    Response.Party.Person(
        id = persone.id.toString(),
        name = persone.name,
        identifier = Response.Party.Person.Identifier.fromDomain(persone.identifier),
        title = persone.title,
        businessFunctions = persone.businessFunctions
            .map { Response.Party.Person.BusinessFunction.fromDomain(it) }
    )

fun Response.Party.Person.Identifier.Companion.fromDomain(identifier: Persone.Identifier): Response.Party.Person.Identifier {
    return Response.Party.Person.Identifier(
        id = identifier.id,
        scheme = identifier.scheme,
        uri = identifier.uri
    )
}

fun Response.Party.Person.BusinessFunction.Companion.fromDomain(businessFunction: BusinessFunction): Response.Party.Person.BusinessFunction {
    return Response.Party.Person.BusinessFunction(
        id = businessFunction.id,
        type = businessFunction.type,
        jobTitle = businessFunction.jobTitle,
        period = Response.Party.Person.BusinessFunction.Period.fromDomain(businessFunction.period),
        documents = businessFunction.documents.orEmpty()
            .map { Response.Party.Person.BusinessFunction.Document.fromDomain(it) }
    )
}

fun Response.Party.Person.BusinessFunction.Period.Companion.fromDomain(period: BusinessFunction.Period) =
    Response.Party.Person.BusinessFunction.Period(startDate = period.startDate)

fun Response.Party.Person.BusinessFunction.Document.Companion.fromDomain(document: BusinessFunction.Document) =
    Response.Party.Person.BusinessFunction.Document(
        id = document.id,
        title = document.title,
        description = document.description,
        documentType = document.documentType
    )

fun Response.Party.Details.Companion.fromDomain(details: Details) =
    Response.Party.Details(
        typeOfSupplier = details.typeOfSupplier,
        mainEconomicActivities = details.mainEconomicActivities
            ?.map { Response.Party.Details.EconomicActivity.fromDomain(it) },
        scale = details.scale,
        permits = details.permits
            ?.map { Response.Party.Details.Permit.fromDomain(it) },
        bankAccounts = details.bankAccounts
            ?.map { Response.Party.Details.BankAccount.fromDomain(it) },
        legalForm = details.legalForm?.let { Response.Party.Details.LegalForm.fromDomain(it) }
    )

fun Response.Party.Details.EconomicActivity.Companion.fromDomain(mainEconomicActivity: MainEconomicActivity) =
    Response.Party.Details.EconomicActivity(
        scheme = mainEconomicActivity.scheme,
        id = mainEconomicActivity.id,
        description = mainEconomicActivity.description,
        uri = mainEconomicActivity.uri
    )

fun Response.Party.Details.Permit.Companion.fromDomain(permit: Permit) =
    Response.Party.Details.Permit(
        id = permit.id,
        scheme = permit.scheme,
        url = permit.url,
        permitDetails = Response.Party.Details.Permit.PermitDetails.fromDomain(permit.permitDetails)
    )

fun Response.Party.Details.Permit.PermitDetails.Companion.fromDomain(permitDetails: PermitDetails) =
    Response.Party.Details.Permit.PermitDetails(
        issuedBy = Response.Party.Details.Permit.PermitDetails.IssuedBy.fromDomain(permitDetails.issuedBy),
        issuedThought = Response.Party.Details.Permit.PermitDetails.IssuedThought.fromDomain(permitDetails.issuedThought),
        validityPeriod = Response.Party.Details.Permit.PermitDetails.ValidityPeriod.fromDomain(permitDetails.validityPeriod)
    )

fun Response.Party.Details.Permit.PermitDetails.IssuedBy.Companion.fromDomain(issuedBy: IssuedBy) =
    Response.Party.Details.Permit.PermitDetails.IssuedBy(id = issuedBy.id, name = issuedBy.name)

fun Response.Party.Details.Permit.PermitDetails.IssuedThought.Companion.fromDomain(issuedThought: IssuedThought) =
    Response.Party.Details.Permit.PermitDetails.IssuedThought(id = issuedThought.id, name = issuedThought.name)

fun Response.Party.Details.Permit.PermitDetails.ValidityPeriod.Companion.fromDomain(validityPeriod: ValidityPeriod) =
    Response.Party.Details.Permit.PermitDetails.ValidityPeriod(
        startDate = validityPeriod.startDate,
        endDate = validityPeriod.endDate
    )

fun Response.Party.Details.BankAccount.Companion.fromDomain(bankAccount: BankAccount) =
    Response.Party.Details.BankAccount(
        description = bankAccount.description,
        bankName = bankAccount.bankName,
        address = Response.Party.Address.fromDomain(bankAccount.address),
        identifier = Response.Party.Details.BankAccount.Identifier.fromDomain(bankAccount.identifier),
        accountIdentification = Response.Party.Details.BankAccount.AccountIdentification.fromDomain(bankAccount.accountIdentification),
        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
            ?.map { Response.Party.Details.BankAccount.AccountIdentification.fromDomain(it) }
    )

fun Response.Party.Details.BankAccount.Identifier.Companion.fromDomain(identifier: BankAccount.Identifier) =
    Response.Party.Details.BankAccount.Identifier(
        id = identifier.id,
        scheme = identifier.scheme
    )

fun Response.Party.Details.BankAccount.AccountIdentification.Companion.fromDomain(accountIdentification: AccountIdentification) =
    Response.Party.Details.BankAccount.AccountIdentification(
        id = accountIdentification.id,
        scheme = accountIdentification.scheme
    )

fun Response.Party.Details.BankAccount.AccountIdentification.Companion.fromDomain(accountIdentification: AdditionalAccountIdentifier) =
    Response.Party.Details.BankAccount.AccountIdentification(
        id = accountIdentification.id,
        scheme = accountIdentification.scheme
    )

fun Response.Party.Details.LegalForm.Companion.fromDomain(legalForm: LegalForm) =
    Response.Party.Details.LegalForm(
        id = legalForm.id,
        scheme = legalForm.scheme,
        description = legalForm.description,
        uri = legalForm.uri
    )