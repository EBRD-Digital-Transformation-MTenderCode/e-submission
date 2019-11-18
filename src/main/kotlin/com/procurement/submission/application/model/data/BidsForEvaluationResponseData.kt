package com.procurement.submission.application.model.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueSerializer
import com.procurement.submission.infrastructure.bind.money.MoneyDeserializer
import com.procurement.submission.infrastructure.bind.money.MoneySerializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import java.time.LocalDateTime
import java.util.*

data class BidsForEvaluationResponseData(
    val bids: List<Bid>?
) {
    data class Bid(
        val id: UUID,
        val date: LocalDateTime,
        val status: Status,
        val statusDetails: StatusDetails,
        val tenderers: List<Tenderer>,
        val value: Money,
        val documents: List<Document>?,
        val requirementResponses: List<RequirementResponse>?,
        val relatedLots: List<String>
    ) {
        data class Tenderer(
            val id: String,
            val name: String,
            val identifier: Identifier,
            val additionalIdentifiers: List<AdditionalIdentifier>?,
            val address: Address,
            val contactPoint: ContactPoint,
            val persones: List<Persone>?,
            val details: Details
        ) {
            data class ContactPoint(
                val name: String,
                val email: String,
                val telephone: String,
                val faxNumber: String?,
                val url: String?
            )

            data class Identifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

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

                    data class Country(
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String
                    )

                    data class Region(
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String
                    )
                }
            }

            data class Persone(
                val title: String,
                val name: String,
                val identifier: Identifier,
                val businessFunctions: List<BusinessFunction>
            ) {
                data class BusinessFunction(
                    val id: String,
                    val type: BusinessFunctionType,
                    val jobTitle: String,
                    val period: Period,
                    val documents: List<Document>?
                ) {
                    data class Period(
                        val startDate: LocalDateTime
                    )

                    data class Document(
                        val id: String,
                        val documentType: BusinessFunctionDocumentType,
                        val title: String,
                        val description: String?
                    )
                }

                data class Identifier(
                    val scheme: String,
                    val id: String,
                    val uri: String?
                )
            }

            data class AdditionalIdentifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

            data class Details(
                val typeOfSupplier: TypeOfSupplier,
                val mainEconomicActivities: List<String>,
                val scale: Scale,
                val permits: List<Permit>?,
                val bankAccounts: List<BankAccount>?,
                val legalForm: LegalForm?
            ) {
                data class Permit(
                    val scheme: String,
                    val id: String,
                    val url: String?,
                    val permitDetails: PermitDetails
                ) {
                    data class PermitDetails(
                        val issuedBy: IssuedBy,
                        val issuedThought: IssuedThought,
                        val validityPeriod: ValidityPeriod
                    ) {
                        data class ValidityPeriod(
                            val startDate: LocalDateTime,
                            val endDate: LocalDateTime?
                        )

                        data class IssuedThought(
                            val id: String,
                            val name: String
                        )

                        data class IssuedBy(
                            val id: String,
                            val name: String
                        )
                    }
                }

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
                    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                ) {
                    data class Identifier(
                        val scheme: String,
                        val id: String
                    )

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

                            data class Locality(
                                val scheme: String,
                                val id: String,
                                val description: String,
                                val uri: String?
                            )
                        }
                    }

                    data class AdditionalAccountIdentifier(
                        val scheme: String,
                        val id: String
                    )

                    data class AccountIdentification(
                        val scheme: String,
                        val id: String
                    )
                }
            }
        }

        data class Document(
            val id: String,
            val documentType: DocumentType,
            val title: String?,
            val description: String?,
            val relatedLots: List<String>?
        )

        data class RequirementResponse(
            val id: String,
            val title: String,
            val description: String?,
            val value: RequirementRsValue,
            val requirement: Requirement,
            val period: Period?
        ) {
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