package com.procurement.submission.infrastructure.dto.bid.bidsbylots.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.domain.model.DocumentId
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueSerializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

data class GetBidsByLotsResponse(
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: List<Bid>
) {
    data class Bid(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: BidId,

        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

        @param:JsonProperty("status") @field:JsonProperty("status") val status: Status,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: StatusDetails,
        @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>,

        @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>
    ) {
        data class Tenderer(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String?,

            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("address") @field:JsonProperty("address") val address: Address?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>,

            @param:JsonProperty("details") @field:JsonProperty("details") val details: Details
        ) {
            data class Identifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class AdditionalIdentifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class Address(
                @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                    @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                    @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                ) {
                    data class Country(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    )

                    data class Region(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    )

                    data class Locality(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )
                }
            }

            data class ContactPoint(
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("email") @field:JsonProperty("email") val email: String,

                @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
            )

            data class Persone(
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )

                data class BusinessFunction(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                    @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
                ) {
                    data class Period(
                        @JsonDeserialize(using = JsonDateDeserializer::class)
                        @JsonSerialize(using = JsonDateSerializer::class)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                    )

                    data class Document(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: BusinessFunctionDocumentType,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    )
                }
            }

            data class Details(
                @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: String?,
                @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<String>,
                @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
            ) {
                data class Permit(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,
                    @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
                ) {
                    data class PermitDetails(
                        @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                        @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                        @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                    ) {
                        data class IssuedBy(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        )

                        data class IssuedThought(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        )

                        data class ValidityPeriod(
                            @JsonDeserialize(using = JsonDateDeserializer::class)
                            @JsonSerialize(using = JsonDateSerializer::class)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @JsonDeserialize(using = JsonDateDeserializer::class)
                            @JsonSerialize(using = JsonDateSerializer::class)
                            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                        )
                    }
                }

                data class BankAccount(
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                    @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
                ) {
                    data class Address(
                        @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,
                        @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) {
                        data class AddressDetails(
                            @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                            @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                            @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                        ) {
                            data class Country(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Region(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Locality(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                            )
                        }
                    }

                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class AccountIdentification(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class AdditionalAccountIdentifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )
                }

                data class LegalForm(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )
            }
        }

        data class Value(
            @param:JsonProperty("amount") @field:JsonProperty("amount") val amount: BigDecimal,
            @param:JsonProperty("currency") @field:JsonProperty("currency") val currency: String
        )

        data class Document(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String?,

            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>
        )

        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementRsValue,

            @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
        ) {
            data class Requirement(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )

            data class Period(
                @JsonDeserialize(using = JsonDateDeserializer::class)
                @JsonSerialize(using = JsonDateSerializer::class)
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                @JsonDeserialize(using = JsonDateDeserializer::class)
                @JsonSerialize(using = JsonDateSerializer::class)
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
            )
        }
    }
}
