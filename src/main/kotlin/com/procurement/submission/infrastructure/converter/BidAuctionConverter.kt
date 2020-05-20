package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidsAuctionRequestData
import com.procurement.submission.application.model.data.BidsAuctionResponseData
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.domain.model.requirement.RequirementId
import com.procurement.submission.domain.model.requirement.RequirementResponseId
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.request.GetBidsAuctionRequest
import com.procurement.submission.model.dto.response.GetBidsAuctionResponse
import java.time.LocalDateTime
import java.util.*

data class BidData(
    val owner: UUID,
    val bid: Bid
)

fun GetBidsAuctionRequest.convert(): BidsAuctionRequestData {
    return BidsAuctionRequestData(
        lots = this.lots
            .map { lot ->
                BidsAuctionRequestData.Lot(
                    id = lot.id
                )
            }
    )
}

fun BidsAuctionResponseData.convert(): GetBidsAuctionResponse {
    return GetBidsAuctionResponse(
        bidsData = this.bidsData
            ?.map { bidData ->
                GetBidsAuctionResponse.BidsData(
                    owner = bidData.owner,
                    bids = bidData.bids
                        .map { bid ->
                            GetBidsAuctionResponse.BidsData.Bid(
                                id = bid.id,
                                pendingDate = bid.pendingDate,
                                date = bid.date,
                                status = bid.status,
                                statusDetails = bid.statusDetails,
                                tenderers = bid.tenderers
                                    .map { tenderer ->
                                        GetBidsAuctionResponse.BidsData.Bid.Tenderer(
                                            id = tenderer.id,
                                            name = tenderer.name,
                                            identifier = tenderer.identifier
                                                .let { identifier ->
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.Identifier(
                                                        id = tenderer.identifier.id,
                                                        scheme = tenderer.identifier.scheme,
                                                        legalName = tenderer.identifier.legalName,
                                                        uri = tenderer.identifier.uri
                                                    )
                                                },
                                            additionalIdentifiers = tenderer.additionalIdentifiers
                                                ?.map { additionalIdentifiers ->
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.AdditionalIdentifier(
                                                        id = additionalIdentifiers.id,
                                                        scheme = additionalIdentifiers.scheme,
                                                        legalName = additionalIdentifiers.legalName,
                                                        uri = additionalIdentifiers.uri
                                                    )
                                                },
                                            address = tenderer.address
                                                .let { address ->
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails
                                                            .let { addressDetails ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails(
                                                                    country = addressDetails.country
                                                                        .let { country ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Country(
                                                                                id = country.id,
                                                                                scheme = country.scheme,
                                                                                description = country.description,
                                                                                uri = country.uri
                                                                            )
                                                                        },
                                                                    region = addressDetails.region
                                                                        .let { region ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Region(
                                                                                id = region.id,
                                                                                scheme = region.scheme,
                                                                                description = region.description,
                                                                                uri = region.uri
                                                                            )
                                                                        },
                                                                    locality = addressDetails.locality
                                                                        .let { locality ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Locality(
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
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.ContactPoint(
                                                        name = contactPoint.name,
                                                        email = contactPoint.email,
                                                        telephone = contactPoint.telephone,
                                                        faxNumber = contactPoint.faxNumber,
                                                        url = contactPoint.url
                                                    )
                                                },
                                            details = tenderer.details
                                                .let { details ->
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details(
                                                        typeOfSupplier = details.typeOfSupplier,
                                                        mainEconomicActivities = details.mainEconomicActivities
                                                            ?.map { mainEconomicActivity ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.MainEconomicActivity(
                                                                    id = mainEconomicActivity.id,
                                                                    description = mainEconomicActivity.description,
                                                                    uri = mainEconomicActivity.uri,
                                                                    scheme = mainEconomicActivity.scheme
                                                                )
                                                            },
                                                        scale = details.scale,
                                                        permits = details.permits
                                                            ?.map { permit ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.Permit(
                                                                    id = permit.id,
                                                                    scheme = permit.scheme,
                                                                    url = permit.url,
                                                                    permitDetails = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.Permit.PermitDetails(
                                                                        issuedBy = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                            id = permit.permitDetails.issuedBy.id,
                                                                            name = permit.permitDetails.issuedBy.name
                                                                        ),
                                                                        issuedThought = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                            id = permit.permitDetails.issuedThought.id,
                                                                            name = permit.permitDetails.issuedThought.name
                                                                        ),
                                                                        validityPeriod = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                                                            endDate = permit.permitDetails.validityPeriod.endDate
                                                                        )
                                                                    )
                                                                )
                                                            },

                                                        bankAccounts = details.bankAccounts
                                                            ?.map { bankAccount ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount(
                                                                    description = bankAccount.description,
                                                                    bankName = bankAccount.bankName,
                                                                    identifier = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Identifier(
                                                                        id = bankAccount.identifier.id,
                                                                        scheme = bankAccount.identifier.scheme
                                                                    ),
                                                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                                        ?.map { additionalIdentifier ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                                id = additionalIdentifier.id,
                                                                                scheme = additionalIdentifier.scheme
                                                                            )
                                                                        },
                                                                    accountIdentification = bankAccount.accountIdentification
                                                                        .let { accountIdentification ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                                                id = accountIdentification.id,
                                                                                scheme = accountIdentification.scheme
                                                                            )
                                                                        },
                                                                    address = bankAccount.address
                                                                        .let { address ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address(
                                                                                streetAddress = address.streetAddress,
                                                                                postalCode = address.postalCode,
                                                                                addressDetails = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                    country = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                        id = address.addressDetails.country.id,
                                                                                        scheme = address.addressDetails.country.scheme,
                                                                                        description = address.addressDetails.country.description,
                                                                                        uri = address.addressDetails.country.uri
                                                                                    ),
                                                                                    region = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                        id = address.addressDetails.region.id,
                                                                                        scheme = address.addressDetails.region.scheme,
                                                                                        description = address.addressDetails.region.description,
                                                                                        uri = address.addressDetails.region.uri
                                                                                    ),
                                                                                    locality = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                                        id = address.addressDetails.locality.id,
                                                                                        scheme = address.addressDetails.locality.scheme,
                                                                                        description = address.addressDetails.locality.description,
                                                                                        uri = address.addressDetails.locality.uri
                                                                                    )
                                                                                )

                                                                            )
                                                                        }
                                                                )
                                                            },
                                                        legalForm = details.legalForm
                                                            ?.let { legalform ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.LegalForm(
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
                                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone(
                                                        title = person.title,
                                                        identifier = person.identifier
                                                            .let { identifier ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.Identifier(
                                                                    id = identifier.id,
                                                                    scheme = identifier.scheme,
                                                                    uri = identifier.uri
                                                                )
                                                            },
                                                        name = person.name,
                                                        businessFunctions = person.businessFunctions
                                                            .map { businessFunction ->
                                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.BusinessFunction(
                                                                    id = businessFunction.id,
                                                                    jobTitle = businessFunction.jobTitle,
                                                                    type = businessFunction.type,
                                                                    period = businessFunction.period
                                                                        .let { period ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                                                startDate = period.startDate
                                                                            )
                                                                        },
                                                                    documents = businessFunction.documents
                                                                        ?.map { document ->
                                                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                                    ?.map { document ->
                                        GetBidsAuctionResponse.BidsData.Bid.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            description = document.description,
                                            title = document.title,
                                            relatedLots = document.relatedLots
                                        )
                                    },
                                requirementResponses = bid.requirementResponses
                                    ?.map { requirementResponse ->
                                        GetBidsAuctionResponse.BidsData.Bid.RequirementResponse(
                                            id = requirementResponse.id,
                                            description = requirementResponse.description,
                                            title = requirementResponse.title,
                                            value = requirementResponse.value,
                                            period = requirementResponse.period
                                                ?.let { period ->
                                                    GetBidsAuctionResponse.BidsData.Bid.RequirementResponse.Period(
                                                        startDate = period.startDate,
                                                        endDate = period.endDate
                                                    )
                                                },
                                            requirement = requirementResponse.requirement
                                                .let { requirement ->
                                                    GetBidsAuctionResponse.BidsData.Bid.RequirementResponse.Requirement(
                                                        id = requirement.id
                                                    )
                                                }
                                        )
                                    },
                                relatedLots = bid.relatedLots
                            )
                        }
                )
            }
    )
}

fun Bid.convert(pendingDate: LocalDateTime): BidsAuctionResponseData.BidsData.Bid {
    return BidsAuctionResponseData.BidsData.Bid(
        id = BidId.fromString(this.id),
        pendingDate = pendingDate,
        date = this.date,
        status = this.status,
        statusDetails = this.statusDetails,
        tenderers = this.tenderers
            .map { tenderer ->
                BidsAuctionResponseData.BidsData.Bid.Tenderer(
                    id = tenderer.id!!,
                    name = tenderer.name,
                    identifier = tenderer.identifier
                        .let { identifier ->
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Identifier(
                                id = identifier.id,
                                scheme = identifier.scheme,
                                legalName = identifier.legalName,
                                uri = identifier.uri
                            )
                        },
                    additionalIdentifiers = tenderer.additionalIdentifiers
                        ?.map { additionalIdentifiers ->
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                    address = tenderer.address
                        .let { address ->
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Address(
                                streetAddress = address.streetAddress,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails
                                    .let { addressDetails ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails(
                                            country = addressDetails.country
                                                .let { country ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Country(
                                                        id = country.id,
                                                        scheme = country.scheme,
                                                        description = country.description,
                                                        uri = country.uri
                                                    )
                                                },
                                            region = addressDetails.region
                                                .let { region ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Region(
                                                        id = region.id,
                                                        scheme = region.scheme,
                                                        description = region.description,
                                                        uri = region.uri
                                                    )
                                                },
                                            locality = addressDetails.locality
                                                .let { locality ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Locality(
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
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.ContactPoint(
                                name = contactPoint.name,
                                email = contactPoint.email!!,
                                telephone = contactPoint.telephone,
                                faxNumber = contactPoint.faxNumber,
                                url = contactPoint.url
                            )
                        },
                    details = tenderer.details
                        .let { details ->
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details(
                                typeOfSupplier = details.typeOfSupplier
                                    ?.let { TypeOfSupplier.fromString(it) },
                                mainEconomicActivities = details.mainEconomicActivities
                                    ?.map { mainEconomicActivity ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.MainEconomicActivity(
                                            id = mainEconomicActivity.id,
                                            description = mainEconomicActivity.description,
                                            uri = mainEconomicActivity.uri,
                                            scheme = mainEconomicActivity.scheme
                                        )
                                    },
                                scale = Scale.fromString(details.scale),
                                permits = details.permits
                                    ?.map { permit ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit(
                                            id = permit.id,
                                            scheme = permit.scheme,
                                            url = permit.url,
                                            permitDetails = permit.permitDetails
                                                .let { permitDetails ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails(
                                                        issuedBy = permitDetails.issuedBy
                                                            .let { issuedBy ->
                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                    id = issuedBy.id,
                                                                    name = issuedBy.name
                                                                )
                                                            },
                                                        issuedThought = permitDetails.issuedThought
                                                            .let { issuedThought ->
                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                    id = issuedThought.id,
                                                                    name = issuedThought.name
                                                                )
                                                            },
                                                        validityPeriod = permitDetails.validityPeriod
                                                            .let { validityPeriod ->
                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                    startDate = validityPeriod.startDate,
                                                                    endDate = validityPeriod.endDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    },

                                bankAccounts = details.bankAccounts
                                    ?.map { bankAccount ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount(
                                            description = bankAccount.description,
                                            bankName = bankAccount.bankName,
                                            identifier = bankAccount.identifier
                                                .let { identifier ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme
                                                    )
                                                },
                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                ?.map { additionalIdentifier ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                        id = additionalIdentifier.id,
                                                        scheme = additionalIdentifier.scheme
                                                    )
                                                },
                                            accountIdentification = bankAccount.accountIdentification
                                                .let { accountIdentification ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                        id = accountIdentification.id,
                                                        scheme = accountIdentification.scheme
                                                    )
                                                },
                                            address = bankAccount.address
                                                .let { address ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails
                                                            .let { addressDetails ->
                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                    country = addressDetails.country
                                                                        .let { country ->
                                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                id = country.id,
                                                                                scheme = country.scheme,
                                                                                description = country.description,
                                                                                uri = country.uri
                                                                            )
                                                                        },
                                                                    region = addressDetails.region
                                                                        .let { region ->
                                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                id = region.id,
                                                                                scheme = region.scheme,
                                                                                description = region.description,
                                                                                uri = region.uri
                                                                            )
                                                                        },
                                                                    locality = addressDetails.locality
                                                                        .let { locality ->
                                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                    ?.let { legalform ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.LegalForm(
                                            id = legalform.id,
                                            scheme = legalform.scheme,
                                            description = legalform.description,
                                            uri = legalform.uri!!
                                        )
                                    }
                            )
                        },
                    persones = tenderer.persones
                        ?.map { person ->
                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = person.identifier
                                    .let { identifier ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.Identifier(
                                            id = identifier.id,
                                            scheme = identifier.scheme,
                                            uri = identifier.uri!!
                                        )
                                    },
                                name = person.name,
                                businessFunctions = person.businessFunctions
                                    .map { businessFunction ->
                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction(
                                            id = businessFunction.id,
                                            jobTitle = businessFunction.jobTitle,
                                            type = businessFunction.type,
                                            period = businessFunction.period
                                                .let { period ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                        startDate = period.startDate
                                                    )
                                                },
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
        value = this.value!!,
        documents = this.documents
            ?.map { document ->
                BidsAuctionResponseData.BidsData.Bid.Document(
                    id = document.id,
                    documentType = document.documentType,
                    description = document.description,
                    title = document.title,
                    relatedLots = document.relatedLots
                        ?.let { documents ->
                            documents.map { LotId.fromString(it) }
                        }
                )
            },
        requirementResponses = this.requirementResponses
            ?.map { requirementResponse ->
                BidsAuctionResponseData.BidsData.Bid.RequirementResponse(
                    id = RequirementResponseId.fromString(requirementResponse.id),
                    description = requirementResponse.description,
                    title = requirementResponse.title,
                    value = requirementResponse.value,
                    period = requirementResponse.period
                        ?.let { period ->
                            BidsAuctionResponseData.BidsData.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                    requirement = requirementResponse.requirement
                        .let { requirement ->
                            BidsAuctionResponseData.BidsData.Bid.RequirementResponse.Requirement(
                                id = RequirementId.fromString(requirement.id)
                            )
                        }
                )
            },
        relatedLots = this.relatedLots
            .map { LotId.fromString(it) }
    )
}

fun List<BidsAuctionResponseData.BidsData>.convert(): BidsAuctionResponseData {
    return BidsAuctionResponseData(
        bidsData = this.map { bidsData ->
            BidsAuctionResponseData.BidsData(
                owner = bidsData.owner,
                bids = bidsData.bids
                    .map { bid ->
                        BidsAuctionResponseData.BidsData.Bid(
                            id = bid.id,
                            pendingDate = bid.pendingDate,
                            date = bid.date,
                            status = bid.status,
                            statusDetails = bid.statusDetails,
                            tenderers = bid.tenderers
                                .map { tenderer ->
                                    BidsAuctionResponseData.BidsData.Bid.Tenderer(
                                        id = tenderer.id,
                                        name = tenderer.name,
                                        identifier = tenderer.identifier
                                            .let { identifier ->
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Identifier(
                                                    id = identifier.id,
                                                    scheme = identifier.scheme,
                                                    legalName = identifier.legalName,
                                                    uri = identifier.uri
                                                )
                                            },
                                        additionalIdentifiers = tenderer.additionalIdentifiers
                                            ?.map { additionalIdentifiers ->
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.AdditionalIdentifier(
                                                    id = additionalIdentifiers.id,
                                                    scheme = additionalIdentifiers.scheme,
                                                    legalName = additionalIdentifiers.legalName,
                                                    uri = additionalIdentifiers.uri
                                                )
                                            },
                                        address = tenderer.address
                                            .let { address ->
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Address(
                                                    streetAddress = address.streetAddress,
                                                    postalCode = address.postalCode,
                                                    addressDetails = address.addressDetails
                                                        .let { addressDetails ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails(
                                                                country = addressDetails.country
                                                                    .let { country ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Country(
                                                                            id = country.id,
                                                                            scheme = country.scheme,
                                                                            description = country.description,
                                                                            uri = country.uri
                                                                        )
                                                                    },
                                                                region = addressDetails.region
                                                                    .let { region ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Region(
                                                                            id = region.id,
                                                                            scheme = region.scheme,
                                                                            description = region.description,
                                                                            uri = region.uri
                                                                        )
                                                                    },
                                                                locality = addressDetails.locality
                                                                    .let { locality ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Address.AddressDetails.Locality(
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
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.ContactPoint(
                                                    name = contactPoint.name,
                                                    email = contactPoint.email,
                                                    telephone = contactPoint.telephone,
                                                    faxNumber = contactPoint.faxNumber,
                                                    url = contactPoint.url
                                                )
                                            },
                                        details = tenderer.details
                                            .let { details ->
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details(
                                                    typeOfSupplier = details.typeOfSupplier,
                                                    mainEconomicActivities = details.mainEconomicActivities,
                                                    scale = details.scale,
                                                    permits = details.permits
                                                        ?.map { permit ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit(
                                                                id = permit.id,
                                                                scheme = permit.scheme,
                                                                url = permit.url,
                                                                permitDetails = BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                        id = permit.permitDetails.issuedBy.id,
                                                                        name = permit.permitDetails.issuedBy.name
                                                                    ),
                                                                    issuedThought = BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                        id = permit.permitDetails.issuedThought.id,
                                                                        name = permit.permitDetails.issuedThought.name
                                                                    ),
                                                                    validityPeriod = BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                        startDate = permit.permitDetails.validityPeriod.startDate,
                                                                        endDate = permit.permitDetails.validityPeriod.endDate
                                                                    )
                                                                )
                                                            )
                                                        },

                                                    bankAccounts = details.bankAccounts
                                                        ?.map { bankAccount ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount(
                                                                description = bankAccount.description,
                                                                bankName = bankAccount.bankName,
                                                                identifier = bankAccount.identifier
                                                                    .let { identifier ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Identifier(
                                                                            id = identifier.id,
                                                                            scheme = identifier.scheme
                                                                        )
                                                                    },
                                                                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                                    ?.map { additionalIdentifier ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                            id = additionalIdentifier.id,
                                                                            scheme = additionalIdentifier.scheme
                                                                        )
                                                                    },
                                                                accountIdentification = bankAccount.accountIdentification
                                                                    .let { accountIdentification ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                                            id = accountIdentification.id,
                                                                            scheme = accountIdentification.scheme
                                                                        )
                                                                    },
                                                                address = bankAccount.address
                                                                    .let { address ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address(
                                                                            streetAddress = address.streetAddress,
                                                                            postalCode = address.postalCode,
                                                                            addressDetails = address.addressDetails
                                                                                .let { addressDetails ->
                                                                                    BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                        country = addressDetails.country
                                                                                            .let { country ->
                                                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                                    id = country.id,
                                                                                                    scheme = country.scheme,
                                                                                                    description = country.description,
                                                                                                    uri = country.uri
                                                                                                )
                                                                                            },
                                                                                        region = addressDetails.region
                                                                                            .let { region ->
                                                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                                    id = region.id,
                                                                                                    scheme = region.scheme,
                                                                                                    description = region.description,
                                                                                                    uri = region.uri
                                                                                                )
                                                                                            },
                                                                                        locality = addressDetails.locality
                                                                                            .let { locality ->
                                                                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                                        ?.let { legalform ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Details.LegalForm(
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
                                                BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone(
                                                    title = person.title,
                                                    identifier = person.identifier
                                                        .let { identifier ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.Identifier(
                                                                id = identifier.id,
                                                                scheme = identifier.scheme,
                                                                uri = identifier.uri
                                                            )
                                                        },
                                                    name = person.name,
                                                    businessFunctions = person.businessFunctions
                                                        .map { businessFunction ->
                                                            BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction(
                                                                id = businessFunction.id,
                                                                jobTitle = businessFunction.jobTitle,
                                                                type = businessFunction.type,
                                                                period = businessFunction.period
                                                                    .let { period ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                                            startDate = period.startDate
                                                                        )
                                                                    },
                                                                documents = businessFunction.documents
                                                                    ?.map { document ->
                                                                        BidsAuctionResponseData.BidsData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                                ?.map { document ->
                                    BidsAuctionResponseData.BidsData.Bid.Document(
                                        id = document.id,
                                        documentType = document.documentType,
                                        description = document.description,
                                        title = document.title,
                                        relatedLots = document.relatedLots
                                    )
                                },
                            requirementResponses = bid.requirementResponses
                                ?.map { requirementResponse ->
                                    BidsAuctionResponseData.BidsData.Bid.RequirementResponse(
                                        id = requirementResponse.id,
                                        description = requirementResponse.description,
                                        title = requirementResponse.title,
                                        value = requirementResponse.value,
                                        period = requirementResponse.period
                                            ?.let { period ->
                                                BidsAuctionResponseData.BidsData.Bid.RequirementResponse.Period(
                                                    startDate = period.startDate,
                                                    endDate = period.endDate
                                                )
                                            },
                                        requirement = requirementResponse.requirement
                                            .let { requirement ->
                                                BidsAuctionResponseData.BidsData.Bid.RequirementResponse.Requirement(
                                                    id = requirement.id
                                                )
                                            }
                                    )
                                },
                            relatedLots = bid.relatedLots
                        )
                    }
            )
        }
    )
}