package com.procurement.submission.application.model.data

import com.procurement.submission.domain.model.EntityBase
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import java.time.LocalDateTime

data class BidCreateData(
    val lot: Lot,
    val bid: Bid
) {
    data class Lot(
        val value: Money
    )

    data class Bid(
        val tenderers: List<Tenderer>,
        val value: Money,
        val documents: List<Document>,
        val requirementResponses: List<RequirementResponse>,
        val relatedLots: List<String>
    ) {
        class Tenderer private constructor(
            override val id: String,
            val identifier: Identifier,
            val name: String,
            val address: Address,
            val additionalIdentifiers: List<AdditionalIdentifier>,
            val contactPoint: ContactPoint,
            val details: Details,
            val persones: List<Persone>

        ) : EntityBase<String>() {

            companion object {
                @JvmStatic
                operator fun invoke(
                    identifier: Identifier,
                    name: String,
                    address: Address,
                    additionalIdentifiers: List<AdditionalIdentifier>,
                    contactPoint: ContactPoint,
                    details: Details,
                    persones: List<Persone>
                ): Tenderer = Tenderer(
                    id = "${identifier.scheme}-${identifier.id}",
                    identifier = identifier,
                    name = name,
                    address = address,
                    additionalIdentifiers = additionalIdentifiers,
                    contactPoint = contactPoint,
                    details = details,
                    persones = persones
                )
            }

            data class Identifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

            data class AdditionalIdentifier(
                override val id: String,
                val scheme: String,
                val legalName: String,
                val uri: String?
            ) : EntityBase<String>()

            data class Address(
                val streetAddress: String,
                val postalCode: String?,
                val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    val country: Country,
                    val region: Region,
                    val locality: Locality
                ) {
                    data class Locality(
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String?
                    )

                    data class Region(
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String
                    )

                    data class Country(
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String
                    )
                }
            }

            data class ContactPoint(
                val name: String,
                val email: String,
                val telephone: String,
                val faxNumber: String?,
                val url: String?
            )

            data class Details(
                val typeOfSupplier: String?,
                val mainEconomicActivities: List<String>,
                val scale: String,
                val permits: List<Permit>,
                val bankAccounts: List<BankAccount>,
                val legalForm: LegalForm?
            ) {
                data class LegalForm(
                    val scheme: String,
                    val id: String,
                    val description: String,
                    val uri: String?
                )

                data class BankAccount(
                    val description: String,
                    val bankName: String,
                    val address: Address,
                    val identifier: Identifier,
                    val accountIdentification: AccountIdentification,
                    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
                ) {
                    data class Identifier(
                        val scheme: String,
                        val id: String
                    )

                    data class AdditionalAccountIdentifier(
                        override val id: String,
                        val scheme: String
                    ) : EntityBase<String>()

                    data class Address(
                        val streetAddress: String,
                        val postalCode: String?,
                        val addressDetails: AddressDetails
                    ) {
                        data class AddressDetails(
                            val country: Country,
                            val region: Region,
                            val locality: Locality
                        ) {
                            data class Locality(
                                val scheme: String,
                                val id: String,
                                val description: String,
                                val uri: String?
                            )

                            data class Region(
                                val scheme: String,
                                val id: String,
                                val description: String,
                                val uri: String
                            )

                            data class Country(
                                val scheme: String,
                                val id: String,
                                val description: String,
                                val uri: String
                            )
                        }
                    }

                    data class AccountIdentification(
                        val scheme: String,
                        val id: String
                    )
                }

                data class Permit(
                    override val id: String,
                    val scheme: String,
                    val url: String?,
                    val permitDetails: PermitDetails
                ) : EntityBase<String>() {
                    data class PermitDetails(
                        val issuedBy: IssuedBy,
                        val issuedThought: IssuedThought,
                        val validityPeriod: ValidityPeriod
                    ) {
                        data class IssuedBy(
                            val id: String,
                            val name: String
                        )

                        data class IssuedThought(
                            val id: String,
                            val name: String
                        )

                        data class ValidityPeriod(
                            val startDate: LocalDateTime,
                            val endDate: LocalDateTime?
                        )
                    }
                }
            }

            data class Persone(
                val title: String,
                val name: String,
                val identifier: Identifier,
                val businessFunctions: List<BusinessFunction>
            ) {
                data class BusinessFunction(
                    override val id: String,
                    val type: BusinessFunctionType,
                    val jobTitle: String,
                    val period: Period,
                    val documents: List<Document>
                ) : EntityBase<String>() {
                    data class Period(
                        val startDate: LocalDateTime
                    )

                    data class Document(
                        override val id: String,
                        val documentType: BusinessFunctionDocumentType,
                        val title: String,
                        val description: String?
                    ) : EntityBase<String>()
                }

                data class Identifier(
                    override val id: String,
                    val scheme: String,
                    val uri: String?
                ) : EntityBase<String>()
            }
        }

        data class Document(
            override val id: String,
            val documentType: DocumentType,
            val title: String?,
            val description: String?,
            val relatedLots: List<String>
        ) : EntityBase<String>()

        data class RequirementResponse(
            override val id: String,
            val title: String,
            val description: String?,
            val value: RequirementRsValue,
            val requirement: Requirement,
            val period: Period?
        ): EntityBase<String>() {
            data class Period(
                val startDate: LocalDateTime,
                val endDate: LocalDateTime
            )

            data class Requirement(
                val id: String
            )
        }
    }
}

