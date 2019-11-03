package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidCreateData
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.lib.errorIfEmpty
import com.procurement.submission.lib.mapIfNotEmpty
import com.procurement.submission.lib.orThrow
import com.procurement.submission.model.dto.request.BidCreateRequest

fun BidCreateRequest.toData(): BidCreateData {
    return BidCreateData(
        bid = BidCreateData.Bid(
            tenderers = this.bid.tenderers.mapIfNotEmpty { tenderer ->
                BidCreateData.Bid.Tenderer(
                    name = tenderer.name,
                    identifier = BidCreateData.Bid.Tenderer.Identifier(
                        id = tenderer.identifier.id,
                        scheme = tenderer.identifier.scheme,
                        legalName = tenderer.identifier.legalName,
                        uri = tenderer.identifier.uri
                    ),
                    additionalIdentifiers = tenderer.additionalIdentifiers.errorIfEmpty {
                        throw ErrorException(
                            error = ErrorType.EMPTY_LIST,
                            message = "The list of Bid.tenderers.additionalIdentifiers cannot be empty"
                        )
                    }?.map { additionalIdentifiers ->
                        BidCreateData.Bid.Tenderer.AdditionalIdentifier(
                            id = additionalIdentifiers.id,
                            scheme = additionalIdentifiers.scheme,
                            legalName = additionalIdentifiers.legalName,
                            uri = additionalIdentifiers.uri
                        )
                    }.orEmpty(),
                    address = BidCreateData.Bid.Tenderer.Address(
                        streetAddress = tenderer.address.streetAddress,
                        postalCode = tenderer.address.postalCode,
                        addressDetails = BidCreateData.Bid.Tenderer.Address.AddressDetails(
                            country = BidCreateData.Bid.Tenderer.Address.AddressDetails.Country(
                                id = tenderer.address.addressDetails.country.id,
                                scheme = tenderer.address.addressDetails.country.scheme,
                                description = tenderer.address.addressDetails.country.description,
                                uri = tenderer.address.addressDetails.country.uri
                            ),
                            region = BidCreateData.Bid.Tenderer.Address.AddressDetails.Region(
                                id = tenderer.address.addressDetails.region.id,
                                scheme = tenderer.address.addressDetails.region.scheme,
                                description = tenderer.address.addressDetails.region.description,
                                uri = tenderer.address.addressDetails.region.uri
                            ),
                            locality = BidCreateData.Bid.Tenderer.Address.AddressDetails.Locality(
                                id = tenderer.address.addressDetails.locality.id,
                                scheme = tenderer.address.addressDetails.locality.scheme,
                                description = tenderer.address.addressDetails.locality.description,
                                uri = tenderer.address.addressDetails.locality.uri
                            )
                        )
                    ),
                    contactPoint = BidCreateData.Bid.Tenderer.ContactPoint(
                        name = tenderer.contactPoint.name,
                        email = tenderer.contactPoint.email,
                        telephone = tenderer.contactPoint.telephone,
                        faxNumber = tenderer.contactPoint.faxNumber,
                        url = tenderer.contactPoint.url
                    ),
                    details = BidCreateData.Bid.Tenderer.Details(
                        typeOfSupplier = tenderer.details.typeOfSupplier,
                        mainEconomicActivities = tenderer.details.mainEconomicActivities.mapIfNotEmpty { it }
                            .orThrow {
                                throw ErrorException(
                                    error = ErrorType.EMPTY_LIST,
                                    message = "The list of Bid.tenderers.details.mainEconomicActivities cannot be empty"
                                )
                            },
                        scale = tenderer.details.scale,
                        permits = tenderer.details.permits.errorIfEmpty {
                            throw ErrorException(
                                error = ErrorType.EMPTY_LIST,
                                message = "The list of Bid.tenderers.details.permits cannot be empty"
                            )
                        }?.map { permit ->
                            BidCreateData.Bid.Tenderer.Details.Permit(
                                id = permit.id,
                                scheme = permit.scheme,
                                url = permit.url,
                                permitDetails = BidCreateData.Bid.Tenderer.Details.Permit.PermitDetails(
                                    issuedBy = BidCreateData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                        id = permit.permitDetails.issuedBy.id,
                                        name = permit.permitDetails.issuedBy.name
                                    ),
                                    issuedThought = BidCreateData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                        id = permit.permitDetails.issuedThought.id,
                                        name = permit.permitDetails.issuedThought.name
                                    ),
                                    validityPeriod = BidCreateData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                        startDate = permit.permitDetails.validityPeriod.startDate,
                                        endDate = permit.permitDetails.validityPeriod.endDate
                                    )
                                )
                            )
                        }.orEmpty(),

                        bankAccounts = tenderer.details.bankAccounts.errorIfEmpty {
                            throw ErrorException(
                                error = ErrorType.EMPTY_LIST,
                                message = "The list of Bid.tenderers.details.bankAccounts cannot be empty"
                            )
                        }?.map { bankAccount ->
                            BidCreateData.Bid.Tenderer.Details.BankAccount(
                                description = bankAccount.description,
                                bankName = bankAccount.bankName,
                                identifier = BidCreateData.Bid.Tenderer.Details.BankAccount.Identifier(
                                    id = bankAccount.identifier.id,
                                    scheme = bankAccount.identifier.scheme
                                ),
                                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.errorIfEmpty {
                                    throw ErrorException(
                                        error = ErrorType.EMPTY_LIST,
                                        message = "The list of Bid.tenderers.details.bankAccounts.additionalAccountIdentifiers cannot be empty"
                                    )
                                }?.map { additionalIdentifier ->
                                    BidCreateData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                        id = additionalIdentifier.id,
                                        scheme = additionalIdentifier.scheme
                                    )
                                }.orEmpty(),
                                accountIdentification = BidCreateData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                    id = bankAccount.accountIdentification.id,
                                    scheme = bankAccount.accountIdentification.scheme
                                ),
                                address = BidCreateData.Bid.Tenderer.Details.BankAccount.Address(
                                    streetAddress = bankAccount.address.streetAddress,
                                    postalCode = bankAccount.address.postalCode,
                                    addressDetails = BidCreateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                        country = BidCreateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                            id = bankAccount.address.addressDetails.country.id,
                                            scheme = bankAccount.address.addressDetails.country.scheme,
                                            description = bankAccount.address.addressDetails.country.description,
                                            uri = bankAccount.address.addressDetails.country.uri
                                        ),
                                        region = BidCreateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                            id = bankAccount.address.addressDetails.region.id,
                                            scheme = bankAccount.address.addressDetails.region.scheme,
                                            description = bankAccount.address.addressDetails.region.description,
                                            uri = bankAccount.address.addressDetails.region.uri
                                        ),
                                        locality = BidCreateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                            id = bankAccount.address.addressDetails.locality.id,
                                            scheme = bankAccount.address.addressDetails.locality.scheme,
                                            description = bankAccount.address.addressDetails.locality.description,
                                            uri = bankAccount.address.addressDetails.locality.uri
                                        )
                                    )

                                )
                            )
                        }.orEmpty(),
                        legalForm = tenderer.details.legalForm?.let { legalform ->
                            BidCreateData.Bid.Tenderer.Details.LegalForm(
                                id = legalform.id,
                                scheme = legalform.scheme,
                                description = legalform.description,
                                uri = legalform.uri
                            )
                        }
                    ),
                    persones = tenderer.persones.errorIfEmpty {
                        throw ErrorException(
                            error = ErrorType.EMPTY_LIST,
                            message = "The list of Bid.tenderers.persones cannot be empty"
                        )
                    }?.map { person ->
                        BidCreateData.Bid.Tenderer.Persone(
                            title = person.title,
                            identifier = BidCreateData.Bid.Tenderer.Persone.Identifier(
                                id = person.identifier.id,
                                scheme = person.identifier.scheme,
                                uri = person.identifier.uri
                            ),
                            name = person.name,
                            businessFunctions = person.businessFunctions.mapIfNotEmpty { businessFunction ->
                                BidCreateData.Bid.Tenderer.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    jobTitle = businessFunction.jobTitle,
                                    type = businessFunction.type,
                                    period = BidCreateData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                        startDate = businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents.errorIfEmpty {
                                        throw ErrorException(
                                            error = ErrorType.EMPTY_LIST,
                                            message = "The list of Bid.tenderers.persones.businessFunctions.documents cannot be empty"
                                        )
                                    }?.map { document ->
                                        BidCreateData.Bid.Tenderer.Persone.BusinessFunction.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            title = document.title,
                                            description = document.description
                                        )
                                    }.orEmpty()
                                )
                            }.orThrow {
                                throw ErrorException(
                                    error = ErrorType.EMPTY_LIST,
                                    message = "The list of Bid.tenderers.persones.businessFunctions cannot be empty"
                                )
                            }
                        )
                    }.orEmpty()
                )
            }.orThrow {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.tenderers cannot be empty"
                )
            },
            value = this.bid.value,
            documents = this.bid.documents.errorIfEmpty {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.documents cannot be empty"
                )
            }?.map { document ->
                BidCreateData.Bid.Document(
                    id = document.id,
                    documentType = document.documentType,
                    title = document.title,
                    description = document.description,
                    relatedLots = document.relatedLots
                        .errorIfEmpty {
                            throw ErrorException(
                                error = ErrorType.EMPTY_LIST,
                                message = "The list of Bid.documents.relatedLots cannot be empty"
                            )
                        }
                        ?.map { it }
                        .orEmpty()
                )
            }.orEmpty(),
            requirementResponses = this.bid.requirementResponses.errorIfEmpty {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.requirementResponse cannot be empty"
                )
            }?.map { requirementResponse ->
                BidCreateData.Bid.RequirementResponse(
                    id = requirementResponse.id,
                    description = requirementResponse.description,
                    title = requirementResponse.title,
                    value = requirementResponse.value,
                    period = requirementResponse.period?.let { period ->
                        BidCreateData.Bid.RequirementResponse.Period(
                            startDate = period.startDate,
                            endDate = period.endDate
                        )
                    },
                    requirement = BidCreateData.Bid.RequirementResponse.Requirement(
                        id = requirementResponse.requirement.id
                    )
                )
            }.orEmpty(),
            relatedLots = this.bid.relatedLots
        ),
        lot = BidCreateData.Lot(
            value = this.lot.value
        )
    )
}