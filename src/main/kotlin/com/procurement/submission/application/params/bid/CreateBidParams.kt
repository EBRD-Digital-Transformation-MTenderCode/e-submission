package com.procurement.submission.application.params.bid

import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.document.DocumentId
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.item.ItemId
import com.procurement.submission.domain.model.requirement.RequirementId
import com.procurement.submission.model.dto.ocds.Amount
import com.procurement.submission.model.dto.ocds.PersonId
import java.time.LocalDateTime

data class CreateBidParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val bids: Bids,
    val date: LocalDateTime,
    val owner: Owner
) {
    data class Bids(
        val details: List<Detail>
    ) {
        data class Detail(
            val id: BidId,
            val value: Value?,
            val tenderers: List<Tenderer>,
            val relatedLots: List<String>,
            val documents: List<Document>,
            val items: List<Item>,
            val requirementResponses: List<RequirementResponse>
        ) {
            data class Value(
                val amount: Amount,
                val currency: String
            )

            data class Tenderer(
                val id: String,
                val name: String,
                val identifier: Identifier,
                val additionalIdentifiers: List<AdditionalIdentifier>,
                val address: Address,
                val contactPoint: ContactPoint,
                val persones: List<Persone>,
                val details: Details
            ) {
                data class Identifier(
                    val id: String,
                    val legalName: String,
                    val scheme: String,
                    val uri: String?
                )

                data class AdditionalIdentifier(
                    val id: String,
                    val legalName: String,
                    val scheme: String,
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
                            val id: String,
                            val description: String,
                            val scheme: String,
                            val uri: String
                        )

                        data class Region(
                            val id: String,
                            val description: String,
                            val scheme: String,
                            val uri: String
                        )

                        data class Locality(
                            val id: String,
                            val description: String,
                            val scheme: String,
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
                    val id: PersonId,
                    val title: PersonTitle,
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
                            val documentType: BusinessFunctionDocumentType,
                            val id: DocumentId,
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
                        val id: String,
                        val scheme: String,
                        val description: String,
                        val uri: String?
                    )

                    data class Permit(
                        val id: String,
                        val scheme: String,
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
                                val endDate: LocalDateTime
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
                                    val id: String,
                                    val description: String,
                                    val scheme: String,
                                    val uri: String
                                )

                                data class Region(
                                    val id: String,
                                    val description: String,
                                    val scheme: String,
                                    val uri: String
                                )

                                data class Locality(
                                    val id: String,
                                    val description: String,
                                    val scheme: String,
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
                val id: DocumentId,
                val title: String,
                val description: String?,
                val relatedLots: List<String>,
                val documentType: DocumentType
            )

            data class Item(
                val id: ItemId,
                val unit: Unit
            ) {
                data class Unit(
                    val id: String,
                    val name: String,
                    val value: Value
                ) {
                    data class Value(
                        val amount: Amount,
                        val currency: String
                    )
                }
            }

            data class RequirementResponse(
                val id: String,
                val value: RequirementRsValue,
                val requirement: Requirement,
                val period: Period?
            ) {
                data class Requirement(
                    val id: RequirementId
                )

                data class Period(
                    val startDate: LocalDateTime,
                    val endDate: LocalDateTime
                )
            }
        }
    }
}