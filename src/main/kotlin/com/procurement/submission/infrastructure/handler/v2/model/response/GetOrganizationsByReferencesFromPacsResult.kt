package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.document.DocumentId
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import java.time.LocalDateTime

data class GetOrganizationsByReferencesFromPacsResult(
    @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
) {
    data class Party(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<Identifier>?,

        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
        @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,
        @param:JsonProperty("roles") @field:JsonProperty("roles") val roles: List<String>,

        @param:JsonProperty("details") @field:JsonProperty("details") val details: Details,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("persones") @field:JsonProperty("persones") val persons: List<Person>?

    ) { companion object {}

        data class Identifier(
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
        ) { companion object {} }

        data class Address(
            @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

            @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
        ) { companion object {}

            data class AddressDetails(
                @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
            ) {

                data class Country(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                )

                data class Region(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                )

                data class Locality(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

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
        ) { companion object {} }

        data class Details(
            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: String?,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<EconomicActivity>?,

            @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?

        ) { companion object {}

            data class EconomicActivity(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            ) { companion object {} }

            data class Permit(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
            ) { companion object {}

                data class PermitDetails(
                    @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                    @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                    @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                ) { companion object {}
                    data class IssuedBy(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) { companion object {} }

                    data class IssuedThought(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                    ) { companion object {} }

                    data class ValidityPeriod(
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                    ) { companion object {} }
                }
            }

            data class BankAccount(
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AccountIdentification>?
            ) { companion object {}

                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                ) { companion object {} }

                data class AccountIdentification(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                ) { companion object {} }
            }

            data class LegalForm(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            ) { companion object {} }
        }

        data class Person(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
            @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
        ) { companion object {}

            data class Identifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            ) { companion object {} }

            data class BusinessFunction(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
            ) { companion object {}

                data class Period(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime?
                ) { companion object {} }

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,
                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: BusinessFunctionDocumentType,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                ) { companion object {} }
            }
        }
    }
}