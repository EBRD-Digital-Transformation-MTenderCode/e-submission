package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.OpenBidsForPublishingData
import com.procurement.submission.application.model.data.OpenBidsForPublishingResult
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.lib.mapIfNotEmpty
import com.procurement.submission.lib.orThrow
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.request.OpenBidsForPublishingRequest
import com.procurement.submission.model.dto.response.OpenBidsForPublishingResponse

fun OpenBidsForPublishingRequest.convert(): OpenBidsForPublishingData {
    return OpenBidsForPublishingData(
        awardCriteriaDetails = this.awardCriteriaDetails,
        awards = this.awards
            .mapIfNotEmpty { award ->
                OpenBidsForPublishingData.Award(
                    statusDetails = award.statusDetails,
                    relatedBid = award.relatedBid
                )
            }
            .orThrow {
                throw ErrorException(
                    error = ErrorType.EMPTY_LIST,
                    message = "The data contains empty list of the awards."
                )
            }
    )
}

fun OpenBidsForPublishingResult.convert(): OpenBidsForPublishingResponse {
    return OpenBidsForPublishingResponse(
        bids = this.bids
            .map { bid ->
                OpenBidsForPublishingResponse.Bid(
                    id = bid.id,
                    date = bid.date,
                    status = bid.status,
                    statusDetails = bid.statusDetails,
                    tenderers = bid.tenderers
                        .map { tenderer ->
                            OpenBidsForPublishingResponse.Bid.Tenderer(
                                id = tenderer.id,
                                name = tenderer.name,
                                identifier = tenderer.identifier
                                    .let { identifier ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.Identifier(
                                            id = identifier.id,
                                            scheme = identifier.scheme,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                additionalIdentifiers = tenderer.additionalIdentifiers
                                    .map { additionalIdentifier ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.AdditionalIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    },
                                address = tenderer.address
                                    .let { address ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails
                                                .let { addressDetails ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails(
                                                        country = addressDetails.country
                                                            .let { country ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Country(
                                                                    id = country.id,
                                                                    scheme = country.scheme,
                                                                    description = country.description,
                                                                    uri = country.uri
                                                                )
                                                            },
                                                        region = addressDetails.region
                                                            .let { region ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Region(
                                                                    id = region.id,
                                                                    scheme = region.scheme,
                                                                    description = region.description,
                                                                    uri = region.uri
                                                                )
                                                            },
                                                        locality = addressDetails.locality
                                                            .let { locality ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Locality(
                                                                    id = locality.id,
                                                                    scheme = locality.scheme,
                                                                    description = locality.description,
                                                                    uri = locality.uri
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    },
                                contactPoint = tenderer.contactPoint
                                    .let { contactPoint ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.ContactPoint(
                                            name = contactPoint.name,
                                            email = contactPoint.email,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            url = contactPoint.url
                                        )
                                    },
                                details = tenderer.details
                                    .let { details ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.Details(
                                            typeOfSupplier = details.typeOfSupplier,
                                            mainEconomicActivities = details.mainEconomicActivities,
                                            scale = details.scale,
                                            permits = details.permits
                                                .map { permit ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit(
                                                        id = permit.id,
                                                        scheme = permit.scheme,
                                                        url = permit.url,
                                                        permitDetails = permit.permitDetails
                                                            .let { permitDetails ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = permitDetails.issuedBy
                                                                        .let { issuedBy ->
                                                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                                id = issuedBy.id,
                                                                                name = issuedBy.name
                                                                            )
                                                                        },
                                                                    issuedThought = permitDetails.issuedThought
                                                                        .let { issuedThought ->
                                                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                                id = issuedThought.id,
                                                                                name = issuedThought.name
                                                                            )
                                                                        },
                                                                    validityPeriod = permitDetails.validityPeriod
                                                                        .let { validityPeriod ->
                                                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                                startDate = validityPeriod.startDate,
                                                                                endDate = validityPeriod.endDate
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                },
                                            bankAccounts = details.bankAccounts
                                                .map { bankAccount ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount(
                                                        description = bankAccount.description,
                                                        bankName = bankAccount.bankName,
                                                        identifier = bankAccount.identifier
                                                            .let { identifier ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                                                    id = identifier.id,
                                                                    scheme = identifier.scheme
                                                                )
                                                            },
                                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                            .map { additionalIdentifier ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                    id = additionalIdentifier.id,
                                                                    scheme = additionalIdentifier.scheme
                                                                )
                                                            },
                                                        accountIdentification = bankAccount.accountIdentification
                                                            .let { accountIdentification ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                                    id = accountIdentification.id,
                                                                    scheme = accountIdentification.scheme
                                                                )
                                                            },
                                                        address = bankAccount.address
                                                            .let { address ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address(
                                                                    streetAddress = address.streetAddress,
                                                                    postalCode = address.postalCode,
                                                                    addressDetails = address.addressDetails
                                                                        .let { addressDetails ->
                                                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                country = addressDetails.country
                                                                                    .let { country ->
                                                                                        OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                            id = country.id,
                                                                                            scheme = country.scheme,
                                                                                            description = country.description,
                                                                                            uri = country.uri
                                                                                        )
                                                                                    },
                                                                                region = addressDetails.region
                                                                                    .let { region ->
                                                                                        OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                            id = region.id,
                                                                                            scheme = region.scheme,
                                                                                            description = region.description,
                                                                                            uri = region.uri
                                                                                        )
                                                                                    },
                                                                                locality = addressDetails.locality
                                                                                    .let { locality ->
                                                                                        OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                                            id = locality.id,
                                                                                            scheme = locality.scheme,
                                                                                            description = locality.description,
                                                                                            uri = locality.uri
                                                                                        )
                                                                                    }
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                },
                                            legalForm = details.legalForm
                                                ?.let { legalForm ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Details.LegalForm(
                                                        id = legalForm.id,
                                                        scheme = legalForm.scheme,
                                                        description = legalForm.description,
                                                        uri = legalForm.uri
                                                    )
                                                }
                                        )
                                    },
                                persones = tenderer.persones
                                    .map { person ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.Persone(
                                            title = person.title,
                                            identifier = person.identifier
                                                .let { identifier ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Persone.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme,
                                                        uri = identifier.uri
                                                    )
                                                },
                                            name = person.name,
                                            businessFunctions = person.businessFunctions
                                                .map { businessFunction ->
                                                    OpenBidsForPublishingResponse.Bid.Tenderer.Persone.BusinessFunction(
                                                        id = businessFunction.id,
                                                        jobTitle = businessFunction.jobTitle,
                                                        type = businessFunction.type,
                                                        period = OpenBidsForPublishingResponse.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                            startDate = businessFunction.period.startDate
                                                        ),
                                                        documents = businessFunction.documents
                                                            .map { document ->
                                                                OpenBidsForPublishingResponse.Bid.Tenderer.Persone.BusinessFunction.Document(
                                                                    id = document.id,
                                                                    documentType = document.documentType,
                                                                    title = document.title,
                                                                    description = document.description
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }
                            )
                        },
                    value = bid.value,
                    documents = bid.documents
                        .map { document ->
                            OpenBidsForPublishingResponse.Bid.Document(
                                id = document.id,
                                documentType = document.documentType,
                                description = document.description,
                                title = document.title,
                                relatedLots = document.relatedLots
                            )
                        },
                    requirementResponses = bid.requirementResponses
                        .map { requirementResponse ->
                            OpenBidsForPublishingResponse.Bid.RequirementResponse(
                                id = requirementResponse.id,
                                description = requirementResponse.description,
                                title = requirementResponse.title,
                                value = requirementResponse.value,
                                period = requirementResponse.period
                                    ?.let { period ->
                                        OpenBidsForPublishingResponse.Bid.RequirementResponse.Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                requirement = OpenBidsForPublishingResponse.Bid.RequirementResponse.Requirement(
                                    id = requirementResponse.requirement.id
                                )
                            )
                        },
                    relatedLots = bid.relatedLots.toList()
                )
            }
    )
}

fun Bid.convert(): OpenBidsForPublishingResult.Bid = this.let { bid ->
    OpenBidsForPublishingResult.Bid(
        id = BidId.fromString(bid.id),
        date = bid.date,
        status = bid.status,
        statusDetails = bid.statusDetails,
        tenderers = bid.tenderers
            .map { tenderer ->
                OpenBidsForPublishingResult.Bid.Tenderer(
                    id = tenderer.id!!,
                    name = tenderer.name,
                    identifier = tenderer.identifier
                        .let { identifier ->
                            OpenBidsForPublishingResult.Bid.Tenderer.Identifier(
                                id = identifier.id,
                                scheme = identifier.scheme,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        },
                    additionalIdentifiers = tenderer.additionalIdentifiers
                        ?.map { additionalIdentifier ->
                            OpenBidsForPublishingResult.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifier.id,
                                scheme = additionalIdentifier.scheme,
                                legalName = additionalIdentifier.legalName,
                                uri = additionalIdentifier.uri
                            )
                        }
                        .orEmpty(),
                    address = tenderer.address
                        .let { address ->
                            OpenBidsForPublishingResult.Bid.Tenderer.Address(
                                streetAddress = address.streetAddress,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails
                                    .let { addressDetails ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails(
                                            country = addressDetails.country
                                                .let { country ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Country(
                                                        id = country.id,
                                                        scheme = country.scheme,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                            region = addressDetails.region
                                                .let { region ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Region(
                                                        id = region.id,
                                                        scheme = region.scheme,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                            locality = addressDetails.locality
                                                .let { locality ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Locality(
                                                        id = locality.id,
                                                        scheme = locality.scheme,
                                                        description = locality.description,
                                                        uri = locality.uri
                                                    )
                                                }
                                        )
                                    }
                            )
                        },
                    contactPoint = tenderer.contactPoint
                        .let { contactPoint ->
                            OpenBidsForPublishingResult.Bid.Tenderer.ContactPoint(
                                name = contactPoint.name,
                                email = contactPoint.email!!,
                                telephone = contactPoint.telephone,
                                faxNumber = contactPoint.faxNumber,
                                url = contactPoint.url
                            )
                        },
                    details = tenderer.details
                        .let { details ->
                            OpenBidsForPublishingResult.Bid.Tenderer.Details(
                                typeOfSupplier = details.typeOfSupplier
                                    ?.let { TypeOfSupplier.fromString(it) },
                                mainEconomicActivities = details.mainEconomicActivities,
                                scale = Scale.fromString(details.scale),
                                permits = details.permits
                                    ?.map { permit ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit(
                                            id = permit.id,
                                            scheme = permit.scheme,
                                            url = permit.url,
                                            permitDetails = permit.permitDetails
                                                .let { permitDetails ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails(
                                                        issuedBy = permitDetails.issuedBy
                                                            .let { issuedBy ->
                                                                OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                    id = issuedBy.id,
                                                                    name = issuedBy.name
                                                                )
                                                            },
                                                        issuedThought = permitDetails.issuedThought
                                                            .let { issuedThought ->
                                                                OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                    id = issuedThought.id,
                                                                    name = issuedThought.name
                                                                )
                                                            },
                                                        validityPeriod = permitDetails.validityPeriod
                                                            .let { validityPeriod ->
                                                                OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                    startDate = validityPeriod.startDate,
                                                                    endDate = validityPeriod.endDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }
                                    .orEmpty(),
                                bankAccounts = details.bankAccounts
                                    ?.map { bankAccount ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount(
                                            description = bankAccount.description,
                                            bankName = bankAccount.bankName,
                                            identifier = bankAccount.identifier
                                                .let { identifier ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme
                                                    )
                                                },
                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                .map { additionalIdentifier ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                        id = additionalIdentifier.id,
                                                        scheme = additionalIdentifier.scheme
                                                    )
                                                },
                                            accountIdentification = bankAccount.accountIdentification
                                                .let { accountIdentification ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                        id = accountIdentification.id,
                                                        scheme = accountIdentification.scheme
                                                    )
                                                },
                                            address = bankAccount.address
                                                .let { address ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails
                                                            .let { addressDetails ->
                                                                OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                    country = addressDetails.country
                                                                        .let { country ->
                                                                            OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                id = country.id,
                                                                                scheme = country.scheme,
                                                                                description = country.description,
                                                                                uri = country.uri
                                                                            )
                                                                        },
                                                                    region = addressDetails.region
                                                                        .let { region ->
                                                                            OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                id = region.id,
                                                                                scheme = region.scheme,
                                                                                description = region.description,
                                                                                uri = region.uri
                                                                            )
                                                                        },
                                                                    locality = addressDetails.locality
                                                                        .let { locality ->
                                                                            OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                                id = locality.id,
                                                                                scheme = locality.scheme,
                                                                                description = locality.description,
                                                                                uri = locality.uri
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }
                                    .orEmpty(),
                                legalForm = details.legalForm
                                    ?.let { legalform ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Details.LegalForm(
                                            id = legalform.id,
                                            scheme = legalform.scheme,
                                            description = legalform.description,
                                            uri = legalform.uri
                                        )
                                    }
                            )
                        },
                    persones = tenderer.persones
                        ?.map { person ->
                            OpenBidsForPublishingResult.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = person.identifier
                                    .let { identifier ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Persone.Identifier(
                                            id = identifier.id,
                                            scheme = identifier.scheme,
                                            uri = identifier.uri
                                        )
                                    },
                                name = person.name,
                                businessFunctions = person.businessFunctions
                                    .map { businessFunction ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction(
                                            id = businessFunction.id,
                                            jobTitle = businessFunction.jobTitle,
                                            type = businessFunction.type,
                                            period = businessFunction.period
                                                .let { period ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                        startDate = period.startDate
                                                    )
                                                },
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction.Document(
                                                        id = document.id,
                                                        documentType = document.documentType,
                                                        title = document.title,
                                                        description = document.description
                                                    )
                                                }
                                                .orEmpty()
                                        )
                                    }
                            )
                        }
                        .orEmpty()
                )
            },
        value = bid.value!!,
        documents = bid.documents
            ?.map { document ->
                OpenBidsForPublishingResult.Bid.Document(
                    id = document.id,
                    documentType = document.documentType,
                    description = document.description,
                    title = document.title,
                    relatedLots = document.relatedLots
                        ?.map { LotId.fromString(it) }
                        .orEmpty()
                )
            }
            .orEmpty(),
        requirementResponses = bid.requirementResponses
            ?.map { requirementResponse ->
                OpenBidsForPublishingResult.Bid.RequirementResponse(
                    id = requirementResponse.id,
                    description = requirementResponse.description,
                    title = requirementResponse.title,
                    value = requirementResponse.value,
                    period = requirementResponse.period
                        ?.let { period ->
                            OpenBidsForPublishingResult.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                    requirement = requirementResponse.requirement
                        .let { requirement ->
                            OpenBidsForPublishingResult.Bid.RequirementResponse.Requirement(
                                id = requirement.id
                            )
                        }

                )
            }
            .orEmpty(),
        relatedLots = bid.relatedLots.map { LotId.fromString(it) }
    )
}
