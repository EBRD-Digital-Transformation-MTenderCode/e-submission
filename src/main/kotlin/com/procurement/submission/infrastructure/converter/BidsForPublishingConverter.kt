package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidsForPublishingRequestData
import com.procurement.submission.application.model.data.BidsForPublishingResponseData
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.request.OpenBidsForPublishingRequest
import com.procurement.submission.model.dto.response.OpenBidsForPublishingResponse
import java.util.*

fun OpenBidsForPublishingRequest.toData() : BidsForPublishingRequestData {
    return BidsForPublishingRequestData(
        awardCriteriaDetails = this.awardCriteriaDetails,
        awards = this.awards.map { award ->
            BidsForPublishingRequestData.Award(
                id = award.id,
                date = award.date,
                statusDetails = award.statusDetails,
                status = award.status,
                relatedLots = award.relatedLots,
                relatedBid = award.relatedBid,
                value = award.value,
                suppliers = award.suppliers.map { supplier ->
                    BidsForPublishingRequestData.Award.Supplier(
                        id = supplier.id,
                        name = supplier.name
                    )
                }
            )
        }
    )
}


fun BidsForPublishingResponseData.toResponse() : OpenBidsForPublishingResponse {
    return OpenBidsForPublishingResponse(
        bids = this.bids.map { bid ->
            OpenBidsForPublishingResponse.Bid(
                id = bid.id,
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    OpenBidsForPublishingResponse.Bid.Tenderer(
                        id = tenderer.id,
                        name = tenderer.name,
                        identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
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
                            permits = tenderer.details.permits?.map { permit ->
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

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalIdentifier ->
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
                            legalForm = tenderer.details.legalForm?.let { legalform ->
                                OpenBidsForPublishingResponse.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            OpenBidsForPublishingResponse.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = OpenBidsForPublishingResponse.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    OpenBidsForPublishingResponse.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = OpenBidsForPublishingResponse.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
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
                documents = bid.documents?.map { document ->
                    OpenBidsForPublishingResponse.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    OpenBidsForPublishingResponse.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        description = requirementResponse.description,
                        title = requirementResponse.title,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
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



fun List<Bid>.toBidsForPublishingResponseData() : BidsForPublishingResponseData {
    return BidsForPublishingResponseData(
        bids = this.map {  bid ->
            BidsForPublishingResponseData.Bid(
                id = UUID.fromString(bid.id),
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    BidsForPublishingResponseData.Bid.Tenderer(
                        id = tenderer.id!!,
                        name = tenderer.name,
                        identifier = BidsForPublishingResponseData.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                            BidsForPublishingResponseData.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                        address = BidsForPublishingResponseData.Bid.Tenderer.Address(
                            streetAddress = tenderer.address.streetAddress,
                            postalCode = tenderer.address.postalCode,
                            addressDetails = BidsForPublishingResponseData.Bid.Tenderer.Address.AddressDetails(
                                country = BidsForPublishingResponseData.Bid.Tenderer.Address.AddressDetails.Country(
                                    id = tenderer.address.addressDetails.country.id,
                                    scheme = tenderer.address.addressDetails.country.scheme,
                                    description = tenderer.address.addressDetails.country.description,
                                    uri = tenderer.address.addressDetails.country.uri
                                ),
                                region = BidsForPublishingResponseData.Bid.Tenderer.Address.AddressDetails.Region(
                                    id = tenderer.address.addressDetails.region.id,
                                    scheme = tenderer.address.addressDetails.region.scheme,
                                    description = tenderer.address.addressDetails.region.description,
                                    uri = tenderer.address.addressDetails.region.uri
                                ),
                                locality = BidsForPublishingResponseData.Bid.Tenderer.Address.AddressDetails.Locality(
                                    id = tenderer.address.addressDetails.locality.id,
                                    scheme = tenderer.address.addressDetails.locality.scheme,
                                    description = tenderer.address.addressDetails.locality.description,
                                    uri = tenderer.address.addressDetails.locality.uri
                                )
                            )
                        ),
                        contactPoint = BidsForPublishingResponseData.Bid.Tenderer.ContactPoint(
                            name = tenderer.contactPoint.name,
                            email = tenderer.contactPoint.email!!,
                            telephone = tenderer.contactPoint.telephone,
                            faxNumber = tenderer.contactPoint.faxNumber,
                            url = tenderer.contactPoint.url
                        ),
                        details = BidsForPublishingResponseData.Bid.Tenderer.Details(
                            typeOfSupplier = TypeOfSupplier.fromString(tenderer.details.typeOfSupplier),
                            mainEconomicActivities = tenderer.details.mainEconomicActivities,
                            scale = Scale.fromString(tenderer.details.scale),
                            permits = tenderer.details.permits?.map { permit ->
                                BidsForPublishingResponseData.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = BidsForPublishingResponseData.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = BidsForPublishingResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = BidsForPublishingResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = BidsForPublishingResponseData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.map { additionalIdentifier ->
                                        BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    },
                                    accountIdentification = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = BidsForPublishingResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                id = bankAccount.address.addressDetails.locality.id,
                                                scheme = bankAccount.address.addressDetails.locality.scheme,
                                                description = bankAccount.address.addressDetails.locality.description,
                                                uri = bankAccount.address.addressDetails.locality.uri
                                            )
                                        )

                                    )
                                )
                            },
                            legalForm = tenderer.details.legalForm?.let { legalform ->
                                BidsForPublishingResponseData.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            BidsForPublishingResponseData.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = BidsForPublishingResponseData.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    BidsForPublishingResponseData.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = BidsForPublishingResponseData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
                                            BidsForPublishingResponseData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                documents = bid.documents?.map { document ->
                    BidsForPublishingResponseData.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    BidsForPublishingResponseData.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        description = requirementResponse.description,
                        title = requirementResponse.title,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            BidsForPublishingResponseData.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = BidsForPublishingResponseData.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                },
                relatedLots = bid.relatedLots
            )
        }
    )
}
