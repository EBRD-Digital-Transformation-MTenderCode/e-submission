package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidCreateData
import com.procurement.submission.application.model.data.BidUpdateData
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.lib.errorIfEmpty
import com.procurement.submission.lib.mapIfNotEmpty
import com.procurement.submission.lib.orThrow
import com.procurement.submission.model.dto.request.BidUpdate
import com.procurement.submission.model.dto.request.BidUpdateRequest

fun BidUpdateRequest.toData(): BidUpdateData {
    return BidUpdateData(
        bid = BidUpdateData.Bid(
            tenderers = this.bid.tenderers.errorIfEmpty {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.tenderers cannot be empty"
                )
            }?.map { tenderer ->
                BidUpdateData.Bid.Tenderer(
                    id = tenderer.id,
                    additionalIdentifiers = tenderer.additionalIdentifiers.errorIfEmpty {
                        throw ErrorException(
                            error = ErrorType.EMPTY_LIST,
                            message = "The list of Bid.tenderers.additionalIdentifiers cannot be empty"
                        )
                    }?.map { additionalIdentifiers ->
                        BidUpdateData.Bid.Tenderer.AdditionalIdentifier(
                            id = additionalIdentifiers.id,
                            scheme = additionalIdentifiers.scheme,
                            legalName = additionalIdentifiers.legalName,
                            uri = additionalIdentifiers.uri
                        )
                    }.orEmpty(),
                    details = tenderer.details?.let { details ->
                        BidUpdateData.Bid.Tenderer.Details(
                            permits = details.permits.errorIfEmpty {
                                throw ErrorException(
                                    error = ErrorType.EMPTY_LIST,
                                    message = "The list of Bid.tenderers.details.permits cannot be empty"
                                )
                            }?.map { permit ->
                                BidUpdateData.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = BidUpdateData.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = BidUpdateData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = BidUpdateData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = BidUpdateData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            }.orEmpty(),
                            bankAccounts = details.bankAccounts.errorIfEmpty {
                                throw ErrorException(
                                    error = ErrorType.EMPTY_LIST,
                                    message = "The list of Bid.tenderers.details.bankAccounts cannot be empty"
                                )
                            }?.map { bankAccount ->
                                BidUpdateData.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = BidUpdateData.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.errorIfEmpty {
                                        throw ErrorException(
                                            error = ErrorType.EMPTY_LIST,
                                            message = "The list of Bid.tenderers.details.bankAccounts.additionalAccountIdentifiers cannot be empty"
                                        )
                                    }?.map { additionalIdentifier ->
                                        BidUpdateData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    }.orEmpty(),
                                    accountIdentification = BidUpdateData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = BidUpdateData.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = BidUpdateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = BidUpdateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = BidUpdateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = BidUpdateData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                id = bankAccount.address.addressDetails.locality.id,
                                                scheme = bankAccount.address.addressDetails.locality.scheme,
                                                description = bankAccount.address.addressDetails.locality.description,
                                                uri = bankAccount.address.addressDetails.locality.uri
                                            )
                                        )

                                    )
                                )
                            }.orEmpty(),
                            legalForm = details.legalForm?.let { legalform ->
                                BidUpdateData.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        )
                    } ,
                    persones = tenderer.persones.errorIfEmpty {
                        throw ErrorException(
                            error = ErrorType.EMPTY_LIST,
                            message = "The list of Bid.tenderers.persones cannot be empty"
                        )
                    }?.map { person ->
                        BidUpdateData.Bid.Tenderer.Persone(
                            title = person.title,
                            identifier = BidUpdateData.Bid.Tenderer.Persone.Identifier(
                                id = person.identifier.id,
                                scheme = person.identifier.scheme,
                                uri = person.identifier.uri
                            ),
                            name = person.name,
                            businessFunctions = person.businessFunctions.mapIfNotEmpty { businessFunction ->
                                BidUpdateData.Bid.Tenderer.Persone.BusinessFunction(
                                    id = businessFunction.id,
                                    jobTitle = businessFunction.jobTitle,
                                    type = businessFunction.type,
                                    period = BidUpdateData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                        startDate = businessFunction.period.startDate
                                    ),
                                    documents = businessFunction.documents.errorIfEmpty {
                                        throw ErrorException(
                                            error = ErrorType.EMPTY_LIST,
                                            message = "The list of Bid.tenderers.persones.businessFunctions.documents cannot be empty"
                                        )
                                    }?.map { document ->
                                        BidUpdateData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
            }.orEmpty(),
            value = this.bid.value,
            documents = this.bid.documents.errorIfEmpty {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.documents cannot be empty"
                )
            }?.map { document ->
                BidUpdateData.Bid.Document(
                    id = document.id,
                    documentType = document.documentType,
                    title = document.title,
                    description = document.description,
                    relatedLots = document.relatedLots
                )
            }.orEmpty(),
            requirementResponses = this.bid.requirementResponses.errorIfEmpty {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The list of Bid.requirementResponse cannot be empty"
                )
            }?.map { requirementResponse ->
                BidUpdateData.Bid.RequirementResponse(
                    id = requirementResponse.id,
                    description = requirementResponse.description,
                    title = requirementResponse.title,
                    value = requirementResponse.value,
                    period = requirementResponse.period?.let { period ->
                        BidUpdateData.Bid.RequirementResponse.Period(
                            startDate = period.startDate,
                            endDate = period.endDate
                        )
                    },
                    requirement = BidUpdateData.Bid.RequirementResponse.Requirement(
                        id = requirementResponse.requirement.id
                    )
                )
            }.orEmpty(),
            relatedLots = this.bid.relatedLots
        ),
        lot = BidUpdateData.Lot(
            value = this.lot.value
        )
    )
}