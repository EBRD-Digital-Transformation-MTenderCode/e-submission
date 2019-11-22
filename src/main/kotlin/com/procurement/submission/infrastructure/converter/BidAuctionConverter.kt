package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidsAuctionRequestData
import com.procurement.submission.application.model.data.BidsAuctionResponseData
import com.procurement.submission.model.dto.request.GetBidsAuctionRequest
import com.procurement.submission.model.dto.response.GetBidsAuctionResponse

fun GetBidsAuctionRequest.toData(): BidsAuctionRequestData {
    return BidsAuctionRequestData(
        lots = this.lots.map { lot ->
            BidsAuctionRequestData.Lot(
                id = lot.id
            )
        }
    )
}

fun BidsAuctionResponseData.toResponse(): GetBidsAuctionResponse {
    return GetBidsAuctionResponse(
        bidsData = this.bidsData?.map { bidData ->
            GetBidsAuctionResponse.BidsData(
                owner = bidData.owner,
                bids = bidData.bids.map { bid ->
                    GetBidsAuctionResponse.BidsData.Bid(
                        id = bid.id,
                        pendingDate = bid.pendingDate,
                        date = bid.date,
                        status = bid.status,
                        statusDetails = bid.statusDetails,
                        tenderers = bid.tenderers.map { tenderer ->
                            GetBidsAuctionResponse.BidsData.Bid.Tenderer(
                                id = tenderer.id,
                                name = tenderer.name,
                                identifier = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Identifier(
                                    id = tenderer.identifier.id,
                                    scheme = tenderer.identifier.scheme,
                                    legalName = tenderer.identifier.legalName,
                                    uri = tenderer.identifier.uri
                                ),
                                additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.AdditionalIdentifier(
                                        id = additionalIdentifiers.id,
                                        scheme = additionalIdentifiers.scheme,
                                        legalName = additionalIdentifiers.legalName,
                                        uri = additionalIdentifiers.uri
                                    )
                                },
                                address = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address(
                                    streetAddress = tenderer.address.streetAddress,
                                    postalCode = tenderer.address.postalCode,
                                    addressDetails = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails(
                                        country = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Country(
                                            id = tenderer.address.addressDetails.country.id,
                                            scheme = tenderer.address.addressDetails.country.scheme,
                                            description = tenderer.address.addressDetails.country.description,
                                            uri = tenderer.address.addressDetails.country.uri
                                        ),
                                        region = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Region(
                                            id = tenderer.address.addressDetails.region.id,
                                            scheme = tenderer.address.addressDetails.region.scheme,
                                            description = tenderer.address.addressDetails.region.description,
                                            uri = tenderer.address.addressDetails.region.uri
                                        ),
                                        locality = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Address.AddressDetails.Locality(
                                            id = tenderer.address.addressDetails.locality.id,
                                            scheme = tenderer.address.addressDetails.locality.scheme,
                                            description = tenderer.address.addressDetails.locality.description,
                                            uri = tenderer.address.addressDetails.locality.uri
                                        )
                                    )
                                ),
                                contactPoint = GetBidsAuctionResponse.BidsData.Bid.Tenderer.ContactPoint(
                                    name = tenderer.contactPoint.name,
                                    email = tenderer.contactPoint.email,
                                    telephone = tenderer.contactPoint.telephone,
                                    faxNumber = tenderer.contactPoint.faxNumber,
                                    url = tenderer.contactPoint.url
                                ),
                                details = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details(
                                    typeOfSupplier = tenderer.details.typeOfSupplier,
                                    mainEconomicActivities = tenderer.details.mainEconomicActivities,
                                    scale = tenderer.details.scale,
                                    permits = tenderer.details.permits?.map { permit ->
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

                                    bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                        GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount(
                                            description = bankAccount.description,
                                            bankName = bankAccount.bankName,
                                            identifier = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Identifier(
                                                id = bankAccount.identifier.id,
                                                scheme = bankAccount.identifier.scheme
                                            ),
                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalIdentifier ->
                                                GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                    id = additionalIdentifier.id,
                                                    scheme = additionalIdentifier.scheme
                                                )
                                            },
                                            accountIdentification = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                id = bankAccount.accountIdentification.id,
                                                scheme = bankAccount.accountIdentification.scheme
                                            ),
                                            address = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address(
                                                streetAddress = bankAccount.address.streetAddress,
                                                postalCode = bankAccount.address.postalCode,
                                                addressDetails = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                    country = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                        id = bankAccount.address.addressDetails.country.id,
                                                        scheme = bankAccount.address.addressDetails.country.scheme,
                                                        description = bankAccount.address.addressDetails.country.description,
                                                        uri = bankAccount.address.addressDetails.country.uri
                                                    ),
                                                    region = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                        id = bankAccount.address.addressDetails.region.id,
                                                        scheme = bankAccount.address.addressDetails.region.scheme,
                                                        description = bankAccount.address.addressDetails.region.description,
                                                        uri = bankAccount.address.addressDetails.region.uri
                                                    ),
                                                    locality = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                        GetBidsAuctionResponse.BidsData.Bid.Tenderer.Details.LegalForm(
                                            id = legalform.id,
                                            scheme = legalform.scheme,
                                            description = legalform.description,
                                            uri = legalform.uri
                                        )
                                    }
                                ),
                                persones = tenderer.persones?.map { person ->
                                    GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone(
                                        title = person.title,
                                        identifier = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.Identifier(
                                            id = person.identifier.id,
                                            scheme = person.identifier.scheme,
                                            uri = person.identifier.uri
                                        ),
                                        name = person.name,
                                        businessFunctions = person.businessFunctions.map { businessFunction ->
                                            GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.BusinessFunction(
                                                id = businessFunction.id,
                                                jobTitle = businessFunction.jobTitle,
                                                type = businessFunction.type,
                                                period = GetBidsAuctionResponse.BidsData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                    startDate = businessFunction.period.startDate
                                                ),
                                                documents = businessFunction.documents?.map { document ->
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
                        documents = bid.documents?.map { document ->
                            GetBidsAuctionResponse.BidsData.Bid.Document(
                                id = document.id,
                                documentType = document.documentType,
                                description = document.description,
                                title = document.title,
                                relatedLots = document.relatedLots
                            )
                        },
                        requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                            GetBidsAuctionResponse.BidsData.Bid.RequirementResponse(
                                id = requirementResponse.id,
                                description = requirementResponse.description,
                                title = requirementResponse.title,
                                value = requirementResponse.value,
                                period = requirementResponse.period?.let { period ->
                                    GetBidsAuctionResponse.BidsData.Bid.RequirementResponse.Period(
                                        startDate = period.startDate,
                                        endDate = period.endDate
                                    )
                                },
                                requirement = GetBidsAuctionResponse.BidsData.Bid.RequirementResponse.Requirement(
                                    id = requirementResponse.requirement.id
                                )
                            )
                        },
                        relatedLots = bid.relatedLots
                    )
                }
            )
        }
    )
}