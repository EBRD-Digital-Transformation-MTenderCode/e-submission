package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.bid.bidsbylots.GetBidsByLotsData
import com.procurement.submission.application.service.bid.bidsbylots.GetBidsByLotsResult
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.infrastructure.dto.bid.bidsbylots.request.GetBidsByLotsRequest
import com.procurement.submission.infrastructure.dto.bid.bidsbylots.response.GetBidsByLotsResponse
import com.procurement.submission.lib.errorIfEmpty
import com.procurement.submission.lib.mapIfNotEmpty
import com.procurement.submission.lib.orThrow

fun GetBidsByLotsRequest.convert() = GetBidsByLotsData(lots = this.lots.map { lot -> GetBidsByLotsData.Lot(id = lot.id) })

fun GetBidsByLotsResult.convert() = GetBidsByLotsResponse(
    bids = this.bids.map { bid ->
        GetBidsByLotsResponse.Bid(
            id = bid.id,
            documents = bid.documents.errorIfEmpty {
                ErrorException(
                    message = "The list of Bid.documents cannot be empty",
                    error = ErrorType.EMPTY_LIST
                )
            }?.map { document ->
                GetBidsByLotsResponse.Bid.Document(
                    id = document.id,
                    relatedLots = document.relatedLots
                        .errorIfEmpty {
                            ErrorException(
                                message = "The list of Bid.documents.relatedLots cannot be empty",
                                error = ErrorType.EMPTY_LIST
                            )
                        }
                        ?.toList()
                        .orEmpty(),
                    description = document.description,
                    title = document.title,
                    documentType = document.documentType
                )
            }.orEmpty(),
            relatedLots = bid.relatedLots
                .toList(),
            tenderers = bid.tenderers
                .mapIfNotEmpty { tender ->
                    GetBidsByLotsResponse.Bid.Tenderer(
                        id = tender.id,
                        name = tender.name,
                        identifier = tender.identifier?.let { identifier ->
                            GetBidsByLotsResponse.Bid.Tenderer.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        },
                        address = tender.address?.let { address ->
                            GetBidsByLotsResponse.Bid.Tenderer.Address(
                                postalCode = address.postalCode,
                                streetAddress = address.streetAddress,
                                addressDetails = address.addressDetails
                                    .let { addressDetail ->
                                        GetBidsByLotsResponse.Bid.Tenderer.Address.AddressDetails(
                                            country = addressDetail.country
                                                .let { country ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Address.AddressDetails.Country(
                                                        id = country.id,
                                                        scheme = country.scheme,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                            locality = addressDetail.locality
                                                .let { locality ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Address.AddressDetails.Locality(
                                                        id = locality.id,
                                                        scheme = locality.scheme,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                },
                                            region = addressDetail.region
                                                .let { region ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Address.AddressDetails.Region(
                                                        id = region.id,
                                                        scheme = region.scheme,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                }
                                        )
                                    }
                            )
                        },
                        details = tender.details
                            .let { detail ->
                                GetBidsByLotsResponse.Bid.Tenderer.Details(
                                    typeOfSupplier = detail.typeOfSupplier,
                                    mainEconomicActivities = detail.mainEconomicActivities,
                                    scale = detail.scale,
                                    permits = detail.permits.errorIfEmpty {
                                        throw ErrorException(
                                            error = ErrorType.EMPTY_LIST,
                                            message = "The list of Bid.tenderers.details.permits cannot be empty"
                                        )
                                    }?.map { permit ->
                                        GetBidsByLotsResponse.Bid.Tenderer.Details.Permit(
                                            id = permit.id,
                                            scheme = permit.scheme,
                                            url = permit.url,
                                            permitDetails = permit.permitDetails.let { permitDetail ->
                                                GetBidsByLotsResponse.Bid.Tenderer.Details.Permit.PermitDetails(
                                                    issuedBy = permitDetail.issuedBy.let { issuedBy ->
                                                        GetBidsByLotsResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                            id = issuedBy.id,
                                                            name = issuedBy.name
                                                        )
                                                    },
                                                    issuedThought = permitDetail.issuedThought.let { issuedThought ->
                                                        GetBidsByLotsResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                            id = issuedThought.id,
                                                            name = issuedThought.name
                                                        )
                                                    },
                                                    validityPeriod = permitDetail.validityPeriod.let { validityPeriod ->
                                                        GetBidsByLotsResponse.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                            startDate = validityPeriod.startDate,
                                                            endDate = validityPeriod.endDate
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    }.orEmpty(),
                                    legalForm = detail.legalForm?.let { legalForm ->
                                        GetBidsByLotsResponse.Bid.Tenderer.Details.LegalForm(
                                            scheme = legalForm.scheme,
                                            id = legalForm.id,
                                            description = legalForm.description,
                                            uri = legalForm.uri
                                        )
                                    },
                                    bankAccounts = detail.bankAccounts
                                        .errorIfEmpty {
                                            throw ErrorException(
                                                error = ErrorType.EMPTY_LIST,
                                                message = "The list of Bid.tenderers.details.bankAccounts cannot be empty"
                                            )
                                        }?.map { bankAccount ->
                                            GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount(
                                                identifier = bankAccount.identifier.let { identifier ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme
                                                    )
                                                },
                                                description = bankAccount.description,
                                                address = bankAccount.address.let { address ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails.let { addressDetail ->
                                                            GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                country = addressDetail.country.let { country ->
                                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                        id = country.id,
                                                                        scheme = country.scheme,
                                                                        description = country.description,
                                                                        uri = country.uri
                                                                    )
                                                                },
                                                                locality = addressDetail.locality.let { locality ->
                                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                        id = locality.id,
                                                                        scheme = locality.scheme,
                                                                        description = locality.description,
                                                                        uri = locality.uri
                                                                    )
                                                                },
                                                                region = addressDetail.region.let { region ->
                                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                        id = region.id,
                                                                        scheme = region.scheme,
                                                                        description = region.description,
                                                                        uri = region.uri
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    )
                                                },
                                                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.errorIfEmpty {
                                                    throw ErrorException(
                                                        error = ErrorType.EMPTY_LIST,
                                                        message = "The list of Bid.tenderers.details.bankAccounts.additionalAccountIdentifiers cannot be empty"
                                                    )
                                                }?.map { additionalAccountIdentifier ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                        id = additionalAccountIdentifier.id,
                                                        scheme = additionalAccountIdentifier.scheme
                                                    )
                                                }.orEmpty(),
                                                accountIdentification = bankAccount.accountIdentification.let { accountIdentification ->
                                                    GetBidsByLotsResponse.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                        scheme = accountIdentification.scheme,
                                                        id = accountIdentification.id
                                                    )
                                                },
                                                bankName = bankAccount.bankName
                                            )
                                        }
                                        .orEmpty()
                                )
                            },
                        contactPoint = tender.contactPoint?.let { contactPoint ->
                            GetBidsByLotsResponse.Bid.Tenderer.ContactPoint(
                                name = contactPoint.name,
                                telephone = contactPoint.telephone,
                                faxNumber = contactPoint.faxNumber,
                                email = contactPoint.email,
                                url = contactPoint.url
                            )
                        },
                        additionalIdentifiers = tender.additionalIdentifiers.errorIfEmpty {
                            throw ErrorException(
                                error = ErrorType.EMPTY_LIST,
                                message = "The list of Bid.tenderers.additionalIdentifiers cannot be empty"
                            )
                        }?.map { additionalIdentifier ->
                            GetBidsByLotsResponse.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifier.id,
                                scheme = additionalIdentifier.scheme,
                                legalName = additionalIdentifier.legalName,
                                uri = additionalIdentifier.uri
                            )
                        }.orEmpty(),
                        persones = tender.persones.errorIfEmpty {
                            throw ErrorException(
                                error = ErrorType.EMPTY_LIST,
                                message = "The list of Bid.tenderers.persones cannot be empty"
                            )
                        }?.map { person ->
                            GetBidsByLotsResponse.Bid.Tenderer.Persone(
                                identifier = person.identifier.let { identifier ->
                                    GetBidsByLotsResponse.Bid.Tenderer.Persone.Identifier(
                                        id = identifier.id,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                                name = person.name,
                                title = person.title,
                                businessFunctions = person.businessFunctions.mapIfNotEmpty { businessFunction ->
                                    GetBidsByLotsResponse.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        type = businessFunction.type,
                                        jobTitle = businessFunction.jobTitle,
                                        documents = businessFunction.documents.errorIfEmpty {
                                            throw ErrorException(
                                                error = ErrorType.EMPTY_LIST,
                                                message = "The list of Bid.tenderers.persones.businessFunctions.documents cannot be empty"
                                            )
                                        }?.map { document ->
                                            GetBidsByLotsResponse.Bid.Tenderer.Persone.BusinessFunction.Document(
                                                id = document.id,
                                                documentType = document.documentType,
                                                title = document.title,
                                                description = document.description
                                            )
                                        }.orEmpty(),
                                        period = businessFunction.period.let { period ->
                                            GetBidsByLotsResponse.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                startDate = period.startDate
                                            )
                                        }
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
            statusDetails = bid.statusDetails,
            status = bid.status,
            requirementResponses = bid.requirementResponses
                .errorIfEmpty {
                    throw ErrorException(
                        error = ErrorType.EMPTY_LIST,
                        message = "The list of Bid.requirementResponse cannot be empty"
                    )
                }?.map { requirementResponse ->
                    GetBidsByLotsResponse.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        description = requirementResponse.description,
                        title = requirementResponse.title,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            GetBidsByLotsResponse.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = GetBidsByLotsResponse.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                }.orEmpty(),
            date = bid.date,
            value = bid.value?.let { value ->
                GetBidsByLotsResponse.Bid.Value(
                    amount = value.amount,
                    currency = value.currency
                )
            }
        )
    }
)