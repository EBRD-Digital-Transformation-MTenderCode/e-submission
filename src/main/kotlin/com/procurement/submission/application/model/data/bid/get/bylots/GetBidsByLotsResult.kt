package com.procurement.submission.application.model.data.bid.get.bylots

import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.document.DocumentId
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.lot.LotId
import java.time.LocalDateTime

class GetBidsByLotsResult(
    val bids: List<Bid>
) {
    data class Bid(
        val id: BidId,
        val date: LocalDateTime,
        val status: BidStatus,
        val statusDetails: BidStatusDetails?,
        val tenderers: List<Tenderer>,
        val value: Money,
        val documents: List<Document>,
        val requirementResponses: List<RequirementResponse>,
        val relatedLots: List<LotId>
    ) {
        data class Tenderer(
            val id: String?,
            val name: String,
            val identifier: Identifier?,
            val additionalIdentifiers: List<AdditionalIdentifier>,
            val address: Address?,
            val contactPoint: ContactPoint?,
            val persones: List<Persone>,
            val details: Details
        ) {
            data class Identifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

            data class AdditionalIdentifier(
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
                    data class Country(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String
                    )

                    data class Region(
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

            data class ContactPoint(
                val name: String,
                val email: String,
                val telephone: String,
                val faxNumber: String?,
                val url: String?
            )

            data class Persone(
                val title: String,
                val name: String,
                val identifier: Identifier,
                val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    val scheme: String,
                    val id: String,
                    val uri: String?
                )

                data class BusinessFunction(
                    val id: String,
                    val type: BusinessFunctionType,
                    val jobTitle: String,
                    val period: Period,
                    val documents: List<Document>
                ) {
                    data class Period(
                        val startDate: LocalDateTime
                    )

                    data class Document(
                        val id: DocumentId,
                        val documentType: BusinessFunctionDocumentType,
                        val title: String,
                        val description: String?
                    )
                }
            }

            data class Details(
                val typeOfSupplier: TypeOfSupplier?,
                val mainEconomicActivities: List<MainEconomicActivity>,
                val scale: Scale,
                val permits: List<Permit>,
                val bankAccounts: List<BankAccount>,
                val legalForm: LegalForm?
            ) {
                data class MainEconomicActivity(
                    val scheme: String,
                    val id: String,
                    val description: String,
                    val uri: String?
                )
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

                data class BankAccount(
                    val description: String,
                    val bankName: String,
                    val address: Address,
                    val identifier: Identifier,
                    val accountIdentification: AccountIdentification,
                    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
                ) {
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
                            data class Country(
                                val scheme: String,
                                val id: String,
                                val description: String,
                                val uri: String
                            )

                            data class Region(
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

                    data class Identifier(
                        val scheme: String,
                        val id: String
                    )

                    data class AccountIdentification(
                        val scheme: String,
                        val id: String
                    )

                    data class AdditionalAccountIdentifier(
                        val scheme: String,
                        val id: String
                    )
                }

                data class LegalForm(
                    val scheme: String,
                    val id: String,
                    val description: String,
                    val uri: String?
                )
            }
        }

        data class Document(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>
        )

        data class RequirementResponse(
            val id: String,
            val value: RequirementRsValue,
            val requirement: Requirement,
            val period: Period?,
            val relatedTenderer: RelatedTenderer?,
            val evidences: List<Evidence>?
        ) {
            data class Requirement(
                val id: String
            )

            data class Period(
                val startDate: LocalDateTime,
                val endDate: LocalDateTime
            )

            data class RelatedTenderer(
                val id: String,
                val name: String
            )

            data class Evidence(
                val id: String,
                val title: String,
                val description: String?,
                val relatedDocument: RelatedDocument?
            ) {
                data class RelatedDocument(
                    val id: DocumentId
                )
            }
        }
    }
}    
