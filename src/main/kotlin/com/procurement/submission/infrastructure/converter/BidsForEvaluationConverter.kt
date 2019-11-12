package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.BidsForEvaluationRequestData
import com.procurement.submission.application.model.data.BidsForEvaludationResponseData
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.request.GetBidsForEvaluationRequest
import com.procurement.submission.model.dto.response.GetBidsForEvaludationResponse
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


fun BidsForEvaludationResponseData.toResponse() : GetBidsForEvaludationResponse {
    return GetBidsForEvaludationResponse(
        bids = this.bids?.map { bid ->
            GetBidsForEvaludationResponse.Bid(
                id = bid.id,
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    GetBidsForEvaludationResponse.Bid.Tenderer(
                        id = tenderer.id,
                        name = tenderer.name,
                        identifier = GetBidsForEvaludationResponse.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                            GetBidsForEvaludationResponse.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                        address = GetBidsForEvaludationResponse.Bid.Tenderer.Address(
                            streetAddress = tenderer.address.streetAddress,
                            postalCode = tenderer.address.postalCode,
                            addressDetails = GetBidsForEvaludationResponse.Bid.Tenderer.Address.AddressDetails(
                                country = GetBidsForEvaludationResponse.Bid.Tenderer.Address.AddressDetails.Country(
                                    id = tenderer.address.addressDetails.country.id,
                                    scheme = tenderer.address.addressDetails.country.scheme,
                                    description = tenderer.address.addressDetails.country.description,
                                    uri = tenderer.address.addressDetails.country.uri
                                ),
                                region = GetBidsForEvaludationResponse.Bid.Tenderer.Address.AddressDetails.Region(
                                    id = tenderer.address.addressDetails.region.id,
                                    scheme = tenderer.address.addressDetails.region.scheme,
                                    description = tenderer.address.addressDetails.region.description,
                                    uri = tenderer.address.addressDetails.region.uri
                                ),
                                locality = GetBidsForEvaludationResponse.Bid.Tenderer.Address.AddressDetails.Locality(
                                    id = tenderer.address.addressDetails.locality.id,
                                    scheme = tenderer.address.addressDetails.locality.scheme,
                                    description = tenderer.address.addressDetails.locality.description,
                                    uri = tenderer.address.addressDetails.locality.uri
                                )
                            )
                        ),
                        contactPoint = GetBidsForEvaludationResponse.Bid.Tenderer.ContactPoint(
                            name = tenderer.contactPoint.name,
                            email = tenderer.contactPoint.email,
                            telephone = tenderer.contactPoint.telephone,
                            faxNumber = tenderer.contactPoint.faxNumber,
                            url = tenderer.contactPoint.url
                        ),
                        details = GetBidsForEvaludationResponse.Bid.Tenderer.Details(
                            typeOfSupplier = tenderer.details.typeOfSupplier,
                            mainEconomicActivities = tenderer.details.mainEconomicActivities,
                            scale = tenderer.details.scale,
                            permits = tenderer.details.permits?.map { permit ->
                                GetBidsForEvaludationResponse.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = GetBidsForEvaludationResponse.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = GetBidsForEvaludationResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = GetBidsForEvaludationResponse.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = GetBidsForEvaludationResponse.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalIdentifier ->
                                        GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    },
                                    accountIdentification = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme,
                                                description = bankAccount.address.addressDetails.country.description,
                                                uri = bankAccount.address.addressDetails.country.uri
                                            ),
                                            region = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme,
                                                description = bankAccount.address.addressDetails.region.description,
                                                uri = bankAccount.address.addressDetails.region.uri
                                            ),
                                            locality = GetBidsForEvaludationResponse.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                GetBidsForEvaludationResponse.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            GetBidsForEvaludationResponse.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = GetBidsForEvaludationResponse.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    GetBidsForEvaludationResponse.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = GetBidsForEvaludationResponse.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
                                            GetBidsForEvaludationResponse.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                    GetBidsForEvaludationResponse.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    GetBidsForEvaludationResponse.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        description = requirementResponse.description,
                        title = requirementResponse.title,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            GetBidsForEvaludationResponse.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = GetBidsForEvaludationResponse.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                },
                relatedLots = bid.relatedLots
            )
        }
    )
}

fun List<Bid>.toResponseData() : BidsForEvaludationResponseData{
    return BidsForEvaludationResponseData(
        bids = this.map {  bid ->
            BidsForEvaludationResponseData.Bid(
                id = UUID.fromString(bid.id),
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers.map { tenderer ->
                    BidsForEvaludationResponseData.Bid.Tenderer(
                        id = tenderer.id!!,
                        name = tenderer.name,
                        identifier = BidsForEvaludationResponseData.Bid.Tenderer.Identifier(
                            id = tenderer.identifier.id,
                            scheme = tenderer.identifier.scheme,
                            legalName = tenderer.identifier.legalName,
                            uri = tenderer.identifier.uri
                        ),
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifiers ->
                            BidsForEvaludationResponseData.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifiers.id,
                                scheme = additionalIdentifiers.scheme,
                                legalName = additionalIdentifiers.legalName,
                                uri = additionalIdentifiers.uri
                            )
                        },
                        address = BidsForEvaludationResponseData.Bid.Tenderer.Address(
                            streetAddress = tenderer.address.streetAddress,
                            postalCode = tenderer.address.postalCode,
                            addressDetails = BidsForEvaludationResponseData.Bid.Tenderer.Address.AddressDetails(
                                country = BidsForEvaludationResponseData.Bid.Tenderer.Address.AddressDetails.Country(
                                    id = tenderer.address.addressDetails.country.id,
                                    scheme = tenderer.address.addressDetails.country.scheme,
                                    description = tenderer.address.addressDetails.country.description,
                                    uri = tenderer.address.addressDetails.country.uri
                                ),
                                region = BidsForEvaludationResponseData.Bid.Tenderer.Address.AddressDetails.Region(
                                    id = tenderer.address.addressDetails.region.id,
                                    scheme = tenderer.address.addressDetails.region.scheme,
                                    description = tenderer.address.addressDetails.region.description,
                                    uri = tenderer.address.addressDetails.region.uri
                                ),
                                locality = BidsForEvaludationResponseData.Bid.Tenderer.Address.AddressDetails.Locality(
                                    id = tenderer.address.addressDetails.locality.id,
                                    scheme = tenderer.address.addressDetails.locality.scheme,
                                    description = tenderer.address.addressDetails.locality.description,
                                    uri = tenderer.address.addressDetails.locality.uri
                                )
                            )
                        ),
                        contactPoint = BidsForEvaludationResponseData.Bid.Tenderer.ContactPoint(
                            name = tenderer.contactPoint.name,
                            email = tenderer.contactPoint.email!!,
                            telephone = tenderer.contactPoint.telephone,
                            faxNumber = tenderer.contactPoint.faxNumber,
                            url = tenderer.contactPoint.url
                        ),
                        details = BidsForEvaludationResponseData.Bid.Tenderer.Details(
                            typeOfSupplier = TypeOfSupplier.fromString(tenderer.details.typeOfSupplier),
                            mainEconomicActivities = tenderer.details.mainEconomicActivities,
                            scale = Scale.fromString(tenderer.details.scale),
                            permits = tenderer.details.permits?.map { permit ->
                                BidsForEvaludationResponseData.Bid.Tenderer.Details.Permit(
                                    id = permit.id,
                                    scheme = permit.scheme,
                                    url = permit.url,
                                    permitDetails = BidsForEvaludationResponseData.Bid.Tenderer.Details.Permit.PermitDetails(
                                        issuedBy = BidsForEvaludationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                            id = permit.permitDetails.issuedBy.id,
                                            name = permit.permitDetails.issuedBy.name
                                        ),
                                        issuedThought = BidsForEvaludationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                            id = permit.permitDetails.issuedThought.id,
                                            name = permit.permitDetails.issuedThought.name
                                        ),
                                        validityPeriod = BidsForEvaludationResponseData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                            startDate = permit.permitDetails.validityPeriod.startDate,
                                            endDate = permit.permitDetails.validityPeriod.endDate
                                        )
                                    )
                                )
                            },

                            bankAccounts = tenderer.details.bankAccounts?.map { bankAccount ->
                                BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount(
                                    description = bankAccount.description,
                                    bankName = bankAccount.bankName,
                                    identifier = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Identifier(
                                        id = bankAccount.identifier.id,
                                        scheme = bankAccount.identifier.scheme
                                    ),
                                    additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.map { additionalIdentifier ->
                                        BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme
                                        )
                                    },
                                    accountIdentification = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                        id = bankAccount.accountIdentification.id,
                                        scheme = bankAccount.accountIdentification.scheme
                                    ),
                                    address = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Address(
                                        streetAddress = bankAccount.address.streetAddress,
                                        postalCode = bankAccount.address.postalCode,
                                        addressDetails = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                            country = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                id = bankAccount.address.addressDetails.country.id,
                                                scheme = bankAccount.address.addressDetails.country.scheme!!,
                                                description = bankAccount.address.addressDetails.country.description!!,
                                                uri = bankAccount.address.addressDetails.country.uri!!
                                            ),
                                            region = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                id = bankAccount.address.addressDetails.region.id,
                                                scheme = bankAccount.address.addressDetails.region.scheme!!,
                                                description = bankAccount.address.addressDetails.region.description!!,
                                                uri = bankAccount.address.addressDetails.region.uri!!
                                            ),
                                            locality = BidsForEvaludationResponseData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
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
                                BidsForEvaludationResponseData.Bid.Tenderer.Details.LegalForm(
                                    id = legalform.id,
                                    scheme = legalform.scheme,
                                    description = legalform.description,
                                    uri = legalform.uri
                                )
                            }
                        ),
                        persones = tenderer.persones?.map { person ->
                            BidsForEvaludationResponseData.Bid.Tenderer.Persone(
                                title = person.title,
                                identifier = BidsForEvaludationResponseData.Bid.Tenderer.Persone.Identifier(
                                    id = person.identifier.id,
                                    scheme = person.identifier.scheme,
                                    uri = person.identifier.uri
                                ),
                                name = person.name,
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    BidsForEvaludationResponseData.Bid.Tenderer.Persone.BusinessFunction(
                                        id = businessFunction.id,
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type,
                                        period = BidsForEvaludationResponseData.Bid.Tenderer.Persone.BusinessFunction.Period(
                                            startDate = businessFunction.period.startDate
                                        ),
                                        documents = businessFunction.documents?.map { document ->
                                            BidsForEvaludationResponseData.Bid.Tenderer.Persone.BusinessFunction.Document(
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
                    BidsForEvaludationResponseData.Bid.Document(
                        id = document.id,
                        documentType = document.documentType,
                        description = document.description,
                        title = document.title,
                        relatedLots = document.relatedLots
                    )
                },
                requirementResponses = bid.requirementResponses?.map { requirementResponse ->
                    BidsForEvaludationResponseData.Bid.RequirementResponse(
                        id = requirementResponse.id,
                        description = requirementResponse.description,
                        title = requirementResponse.title,
                        value = requirementResponse.value,
                        period = requirementResponse.period?.let { period ->
                            BidsForEvaludationResponseData.Bid.RequirementResponse.Period(
                                startDate = period.startDate,
                                endDate = period.endDate
                            )
                        },
                        requirement = BidsForEvaludationResponseData.Bid.RequirementResponse.Requirement(
                            id = requirementResponse.requirement.id
                        )
                    )
                },
                relatedLots = bid.relatedLots
            )
        }
    )
}
