package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.OpenBidsForPublishingData
import com.procurement.submission.application.model.data.OpenBidsForPublishingResult
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.request.OpenBidsForPublishingRequest
import com.procurement.submission.model.dto.response.OpenBidsForPublishingResponse
import java.util.*

fun OpenBidsForPublishingRequest.convert(): OpenBidsForPublishingData {
    return OpenBidsForPublishingData(
        awardCriteriaDetails = this.awardCriteriaDetails,
        awards = this.awards
            .map { award ->
                OpenBidsForPublishingData.Award(
                    statusDetails = award.statusDetails,
                    relatedBid = award.relatedBid
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
                                identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Identifier(
                                    id = tenderer.identifier.id,
                                    scheme = tenderer.identifier.scheme,
                                    legalName = tenderer.identifier.legalName,
                                    uri = tenderer.identifier.uri
                                ),
                                additionalIdentifiers = tenderer.additionalIdentifiers
                                    ?.map { additionalIdentifiers ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.AdditionalIdentifier(
                                            id = additionalIdentifiers.id,
                                            scheme = additionalIdentifiers.scheme,
                                            legalName = additionalIdentifiers.legalName,
                                            uri = additionalIdentifiers.uri
                                        )
                                    },
                                address = OpenBidsForPublishingResponse.Bid.Tenderer.Address(
                                    streetAddress = tenderer.address.streetAddress,
                                    postalCode = tenderer.address.postalCode,
                                    addressDetails = OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails(
                                        country = OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Country(
                                            id = tenderer.address.addressDetails.country.id,
                                            scheme = tenderer.address.addressDetails.country.scheme,
                                            description = tenderer.address.addressDetails.country.description,
                                            uri = tenderer.address.addressDetails.country.uri
                                        ),
                                        region = OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Region(
                                            id = tenderer.address.addressDetails.region.id,
                                            scheme = tenderer.address.addressDetails.region.scheme,
                                            description = tenderer.address.addressDetails.region.description,
                                            uri = tenderer.address.addressDetails.region.uri
                                        ),
                                        locality = OpenBidsForPublishingResponse.Bid.Tenderer.Address.AddressDetails.Locality(
                                            id = tenderer.address.addressDetails.locality.id,
                                            scheme = tenderer.address.addressDetails.locality.scheme,
                                            description = tenderer.address.addressDetails.locality.description,
                                            uri = tenderer.address.addressDetails.locality.uri
                                        )
                                    )
                                ),
                                contactPoint = OpenBidsForPublishingResponse.Bid.Tenderer.ContactPoint(
                                    name = tenderer.contactPoint.name,
                                    email = tenderer.contactPoint.email,
                                    telephone = tenderer.contactPoint.telephone,
                                    faxNumber = tenderer.contactPoint.faxNumber,
                                    url = tenderer.contactPoint.url
                                ),
                                details = OpenBidsForPublishingResponse.Bid.Tenderer.Details(
                                    typeOfSupplier = tenderer.details.typeOfSupplier,
                                    mainEconomicActivities = tenderer.details.mainEconomicActivities,
                                    scale = tenderer.details.scale,
                                    permits = tenderer.details.permits
                                        ?.map { permit ->
                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit(
                                                id = permit.id,
                                                scheme = permit.scheme,
                                                url = permit.url,
                                                permitDetails = OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails(
                                                    issuedBy = OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                        id = permit.permitDetails.issuedBy.id,
                                                        name = permit.permitDetails.issuedBy.name
                                                    ),
                                                    issuedThought = OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                        id = permit.permitDetails.issuedThought.id,
                                                        name = permit.permitDetails.issuedThought.name
                                                    ),
                                                    validityPeriod = OpenBidsForPublishingResponse.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                        startDate = permit.permitDetails.validityPeriod.startDate,
                                                        endDate = permit.permitDetails.validityPeriod.endDate
                                                    )
                                                )
                                            )
                                        },

                                    bankAccounts = tenderer.details.bankAccounts
                                        ?.map { bankAccount ->
                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount(
                                                description = bankAccount.description,
                                                bankName = bankAccount.bankName,
                                                identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                                    id = bankAccount.identifier.id,
                                                    scheme = bankAccount.identifier.scheme
                                                ),
                                                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                    ?.map { additionalIdentifier ->
                                                        OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                            id = additionalIdentifier.id,
                                                            scheme = additionalIdentifier.scheme
                                                        )
                                                    },
                                                accountIdentification = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                    id = bankAccount.accountIdentification.id,
                                                    scheme = bankAccount.accountIdentification.scheme
                                                ),
                                                address = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address(
                                                    streetAddress = bankAccount.address.streetAddress,
                                                    postalCode = bankAccount.address.postalCode,
                                                    addressDetails = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                        country = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                            id = bankAccount.address.addressDetails.country.id,
                                                            scheme = bankAccount.address.addressDetails.country.scheme,
                                                            description = bankAccount.address.addressDetails.country.description,
                                                            uri = bankAccount.address.addressDetails.country.uri
                                                        ),
                                                        region = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                            id = bankAccount.address.addressDetails.region.id,
                                                            scheme = bankAccount.address.addressDetails.region.scheme,
                                                            description = bankAccount.address.addressDetails.region.description,
                                                            uri = bankAccount.address.addressDetails.region.uri
                                                        ),
                                                        locality = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                            id = bankAccount.address.addressDetails.locality.id,
                                                            scheme = bankAccount.address.addressDetails.locality.scheme,
                                                            description = bankAccount.address.addressDetails.locality.description,
                                                            uri = bankAccount.address.addressDetails.locality.uri
                                                        )
                                                    )

                                                )
                                            )
                                        },
                                    legalForm = tenderer.details.legalForm
                                        ?.let { legalform ->
                                            OpenBidsForPublishingResponse.Bid.Tenderer.Details.LegalForm(
                                                id = legalform.id,
                                                scheme = legalform.scheme,
                                                description = legalform.description,
                                                uri = legalform.uri
                                            )
                                        }
                                ),
                                persones = tenderer.persones
                                    ?.map { person ->
                                        OpenBidsForPublishingResponse.Bid.Tenderer.Persone(
                                            title = person.title,
                                            identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Persone.Identifier(
                                                id = person.identifier.id,
                                                scheme = person.identifier.scheme,
                                                uri = person.identifier.uri
                                            ),
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
                                                            ?.map { document ->
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
                        ?.map { document ->
                            OpenBidsForPublishingResponse.Bid.Document(
                                id = document.id,
                                documentType = document.documentType,
                                description = document.description,
                                title = document.title,
                                relatedLots = document.relatedLots
                            )
                        },
                    requirementResponses = bid.requirementResponses
                        ?.map { requirementResponse ->
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
                    relatedLots = bid.relatedLots
                )
            }
    )
}

fun Bid.convert(): OpenBidsForPublishingResult.Bid = this.let { bid ->
    OpenBidsForPublishingResult.Bid(
        id = UUID.fromString(bid.id),
        date = bid.date,
        status = bid.status,
        statusDetails = bid.statusDetails,
        tenderers = bid.tenderers
            .map { tenderer ->
                OpenBidsForPublishingResult.Bid.Tenderer(
                    id = tenderer.id!!,
                    name = tenderer.name,
                    identifier = OpenBidsForPublishingResult.Bid.Tenderer.Identifier(
                        id = tenderer.identifier.id,
                        scheme = tenderer.identifier.scheme,
                        legalName = tenderer.identifier.legalName,
                        uri = tenderer.identifier.uri
                    ),
                    additionalIdentifiers = tenderer.additionalIdentifiers
                        ?.map { additionalIdentifiers ->
                            OpenBidsForPublishingResult.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                    address = OpenBidsForPublishingResult.Bid.Tenderer.Address(
                        streetAddress = tenderer.address.streetAddress,
                        postalCode = tenderer.address.postalCode,
                        addressDetails = OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails(
                            country = OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Country(
                                id = tenderer.address.addressDetails.country.id,
                                scheme = tenderer.address.addressDetails.country.scheme,
                                description = tenderer.address.addressDetails.country.description,
                                uri = tenderer.address.addressDetails.country.uri
                            ),
                            region = OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Region(
                                id = tenderer.address.addressDetails.region.id,
                                scheme = tenderer.address.addressDetails.region.scheme,
                                description = tenderer.address.addressDetails.region.description,
                                uri = tenderer.address.addressDetails.region.uri
                            ),
                            locality = OpenBidsForPublishingResult.Bid.Tenderer.Address.AddressDetails.Locality(
                                id = tenderer.address.addressDetails.locality.id,
                                scheme = tenderer.address.addressDetails.locality.scheme,
                                description = tenderer.address.addressDetails.locality.description,
                                uri = tenderer.address.addressDetails.locality.uri
                            )
                        )
                    ),
                    contactPoint = OpenBidsForPublishingResult.Bid.Tenderer.ContactPoint(
                        name = tenderer.contactPoint.name,
                        email = tenderer.contactPoint.email!!,
                        telephone = tenderer.contactPoint.telephone,
                        faxNumber = tenderer.contactPoint.faxNumber,
                        url = tenderer.contactPoint.url
                    ),
                    details = OpenBidsForPublishingResult.Bid.Tenderer.Details(
                        typeOfSupplier = tenderer.details.typeOfSupplier
                            ?.let { TypeOfSupplier.fromString(it) },
                        mainEconomicActivities = tenderer.details.mainEconomicActivities,
                        scale = Scale.fromString(tenderer.details.scale),
                        permits = tenderer.details.permits
                            ?.map { permit ->
                                OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = OpenBidsForPublishingResult.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                        bankAccounts = tenderer.details.bankAccounts
                            ?.map { bankAccount ->
                                OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                        .map { additionalIdentifier ->
                                            OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                id = additionalIdentifier.id,
                                                scheme = additionalIdentifier.scheme
                                            )
                                        },
                                    accountIdentification = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = OpenBidsForPublishingResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                id = bankAccount.address.addressDetails.locality.id,
                                                scheme = bankAccount.address.addressDetails.locality.scheme,
                                                description = bankAccount.address.addressDetails.locality.description,
                                                uri = bankAccount.address.addressDetails.locality.uri
                                            )
                                        )

                                    )
                                )
                            },
                        legalForm = tenderer.details.legalForm
                            ?.let { legalform ->
                                OpenBidsForPublishingResult.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                    ),
                    persones = tenderer.persones
                        ?.map { person ->
                            OpenBidsForPublishingResult.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = OpenBidsForPublishingResult.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions
                                    .map { businessFunction ->
                                        OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction(
                                            id = businessFunction.id,
                                            jobTitle = businessFunction.jobTitle,
                                            type = businessFunction.type,
                                            period = OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                startDate = businessFunction.period.startDate
                                            ),
                                            documents = businessFunction.documents
                                                ?.map { document ->
                                                    OpenBidsForPublishingResult.Bid.Tenderer.Persone.BusinessFunction.Document(
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
        value = bid.value!!,
        documents = bid.documents
            ?.map { document ->
                OpenBidsForPublishingResult.Bid.Document(
                    id = document.id,
                    documentType = document.documentType,
                    description = document.description,
                    title = document.title,
                    relatedLots = document.relatedLots
                )
            },
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
                    requirement = OpenBidsForPublishingResult.Bid.RequirementResponse.Requirement(
                        id = requirementResponse.requirement.id
                    )
                )
            },
        relatedLots = bid.relatedLots
    )
}

