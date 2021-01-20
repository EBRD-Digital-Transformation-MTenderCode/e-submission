package com.procurement.submission.infrastructure.handler.v1.converter

import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationRequestData
import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationResponseData
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.infrastructure.handler.v1.model.request.GetBidsForEvaluationRequest
import com.procurement.submission.infrastructure.handler.v1.model.response.GetBidsForEvaluationResponse
import com.procurement.submission.model.dto.ocds.Bid
import java.util.*

fun GetBidsForEvaluationRequest.toData() : BidsForEvaluationRequestData {
    return BidsForEvaluationRequestData(
        lots = this.lots.map { lot ->
            BidsForEvaluationRequestData.Lot(
                id = lot.id
            )
        }
    )
}


fun BidsForEvaluationResponseData.toResponse() : GetBidsForEvaluationResponse {
    return GetBidsForEvaluationResponse(
        bids = this.bids?.map { bid ->
            GetBidsForEvaluationResponse.Bid(
                id = bid.id,
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    GetBidsForEvaluationResponse.Bid.Tenderer(
                        id = tenderer.id,
                        name = tenderer.name,
                        identifier = GetBidsForEvaluationResponse.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                            GetBidsForEvaluationResponse.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                        address = GetBidsForEvaluationResponse.Bid.Tenderer.Address(
                            streetAddress = tenderer.address.streetAddress,
                            postalCode = tenderer.address.postalCode,
                            addressDetails = GetBidsForEvaluationResponse.Bid.Tenderer.Address.AddressDetails(
                                country = GetBidsForEvaluationResponse.Bid.Tenderer.Address.AddressDetails.Country(
                                    id = tenderer.address.addressDetails.country.id,
                                    scheme = tenderer.address.addressDetails.country.scheme,
                                    description = tenderer.address.addressDetails.country.description,
                                    uri = tenderer.address.addressDetails.country.uri
                                ),
                                region = GetBidsForEvaluationResponse.Bid.Tenderer.Address.AddressDetails.Region(
                                    id = tenderer.address.addressDetails.region.id,
                                    scheme = tenderer.address.addressDetails.region.scheme,
                                    description = tenderer.address.addressDetails.region.description,
                                    uri = tenderer.address.addressDetails.region.uri
                                ),
                                locality = GetBidsForEvaluationResponse.Bid.Tenderer.Address.AddressDetails.Locality(
                                    id = tenderer.address.addressDetails.locality.id,
                                    scheme = tenderer.address.addressDetails.locality.scheme,
                                    description = tenderer.address.addressDetails.locality.description,
                                    uri = tenderer.address.addressDetails.locality.uri
                                )
                            )
                        ),
                        contactPoint = GetBidsForEvaluationResponse.Bid.Tenderer.ContactPoint(
                            name = tenderer.contactPoint.name,
                            email = tenderer.contactPoint.email,
                            telephone = tenderer.contactPoint.telephone,
                            faxNumber = tenderer.contactPoint.faxNumber,
                            url = tenderer.contactPoint.url
                        ),
                        details = GetBidsForEvaluationResponse.Bid.Tenderer.Details(
                            typeOfSupplier = tenderer.details.typeOfSupplier,
                            mainEconomicActivities = tenderer.details.mainEconomicActivities
                                ?.map { mainEconomicActivity ->
                                    GetBidsForEvaluationResponse.Bid.Tenderer.Details.MainEconomicActivity(
                                        id = mainEconomicActivity.id,
                                        description = mainEconomicActivity.description,
                                        uri = mainEconomicActivity.uri,
                                        scheme = mainEconomicActivity.scheme
                                    )
                                },
                            scale = tenderer.details.scale,
                            permits = tenderer.details.permits?.map { permit ->
                                GetBidsForEvaluationResponse.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = GetBidsForEvaluationResponse.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = GetBidsForEvaluationResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = GetBidsForEvaluationResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = GetBidsForEvaluationResponse.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalIdentifier ->
                                        GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    },
                                    accountIdentification = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = GetBidsForEvaluationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                GetBidsForEvaluationResponse.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            GetBidsForEvaluationResponse.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = GetBidsForEvaluationResponse.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    GetBidsForEvaluationResponse.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = GetBidsForEvaluationResponse.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
                                            GetBidsForEvaluationResponse.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                    GetBidsForEvaluationResponse.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    GetBidsForEvaluationResponse.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            GetBidsForEvaluationResponse.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = GetBidsForEvaluationResponse.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                },
                relatedLots = bid.relatedLots
            )
        }
    )
}

fun Collection<Bid>.toBidsForEvaluationResponseData() : BidsForEvaluationResponseData {
    return BidsForEvaluationResponseData(
        bids = this.map { bid ->
            BidsForEvaluationResponseData.Bid(
                id = UUID.fromString(bid.id),
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    BidsForEvaluationResponseData.Bid.Tenderer(
                        id = tenderer.id!!,
                        name = tenderer.name,
                        identifier = BidsForEvaluationResponseData.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                            BidsForEvaluationResponseData.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                        address = BidsForEvaluationResponseData.Bid.Tenderer.Address(
                            streetAddress = tenderer.address.streetAddress,
                            postalCode = tenderer.address.postalCode,
                            addressDetails = BidsForEvaluationResponseData.Bid.Tenderer.Address.AddressDetails(
                                country = BidsForEvaluationResponseData.Bid.Tenderer.Address.AddressDetails.Country(
                                    id = tenderer.address.addressDetails.country.id,
                                    scheme = tenderer.address.addressDetails.country.scheme,
                                    description = tenderer.address.addressDetails.country.description,
                                    uri = tenderer.address.addressDetails.country.uri
                                ),
                                region = BidsForEvaluationResponseData.Bid.Tenderer.Address.AddressDetails.Region(
                                    id = tenderer.address.addressDetails.region.id,
                                    scheme = tenderer.address.addressDetails.region.scheme,
                                    description = tenderer.address.addressDetails.region.description,
                                    uri = tenderer.address.addressDetails.region.uri
                                ),
                                locality = BidsForEvaluationResponseData.Bid.Tenderer.Address.AddressDetails.Locality(
                                    id = tenderer.address.addressDetails.locality.id,
                                    scheme = tenderer.address.addressDetails.locality.scheme,
                                    description = tenderer.address.addressDetails.locality.description,
                                    uri = tenderer.address.addressDetails.locality.uri
                                )
                            )
                        ),
                        contactPoint = BidsForEvaluationResponseData.Bid.Tenderer.ContactPoint(
                            name = tenderer.contactPoint.name,
                            email = tenderer.contactPoint.email!!,
                            telephone = tenderer.contactPoint.telephone,
                            faxNumber = tenderer.contactPoint.faxNumber,
                            url = tenderer.contactPoint.url
                        ),
                        details = BidsForEvaluationResponseData.Bid.Tenderer.Details(
                            typeOfSupplier = tenderer.details.typeOfSupplier?.let { TypeOfSupplier.creator(it) },
                            mainEconomicActivities = tenderer.details.mainEconomicActivities
                                ?.map { mainEconomicActivity ->
                                    BidsForEvaluationResponseData.Bid.Tenderer.Details.MainEconomicActivity(
                                        id = mainEconomicActivity.id,
                                        description = mainEconomicActivity.description,
                                        uri = mainEconomicActivity.uri,
                                        scheme = mainEconomicActivity.scheme
                                    )
                                },
                            scale = Scale.creator(tenderer.details.scale),
                            permits = tenderer.details.permits?.map { permit ->
                                BidsForEvaluationResponseData.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = BidsForEvaluationResponseData.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = BidsForEvaluationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = BidsForEvaluationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = BidsForEvaluationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalIdentifier ->
                                        BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    },
                                    accountIdentification = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = BidsForEvaluationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                BidsForEvaluationResponseData.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            BidsForEvaluationResponseData.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = BidsForEvaluationResponseData.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    BidsForEvaluationResponseData.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = BidsForEvaluationResponseData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
                                            BidsForEvaluationResponseData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                    BidsForEvaluationResponseData.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    BidsForEvaluationResponseData.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            BidsForEvaluationResponseData.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = BidsForEvaluationResponseData.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                },
                relatedLots = bid.relatedLots
            )
        }
    )
}
