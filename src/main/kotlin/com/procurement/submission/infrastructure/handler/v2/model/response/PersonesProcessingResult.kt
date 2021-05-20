package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Address
import com.procurement.submission.model.dto.ocds.AddressDetails
import com.procurement.submission.model.dto.ocds.BusinessFunction
import com.procurement.submission.model.dto.ocds.ContactPoint
import com.procurement.submission.model.dto.ocds.CountryDetails
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Identifier
import com.procurement.submission.model.dto.ocds.LocalityDetails
import com.procurement.submission.model.dto.ocds.Organization
import com.procurement.submission.model.dto.ocds.PersonId
import com.procurement.submission.model.dto.ocds.Persone
import com.procurement.submission.model.dto.ocds.RegionDetails
import java.time.LocalDateTime

data class PersonesProcessingResult(
    @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
) {
    data class Party(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<AdditionalIdentifier>?,

        @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
        @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Persone>,
    ) {
        data class Identifier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
        )

        data class AdditionalIdentifier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

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
        )

        data class Persone(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>?
        ) {
            data class Identifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class BusinessFunction(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("type") @field:JsonProperty("type") val type: String,
                @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
            ) {
                data class Period(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                )

                data class Document(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                )
            }
        }
    }

    object ResponseConverter {
        fun fromDomain(tenderer: Organization): Party =
            Party(
                id = tenderer.id!!,
                name = tenderer.name,
                identifier = fromDomain(tenderer.identifier),
                additionalIdentifiers = tenderer.additionalIdentifiers
                    ?.map { fromDomainAdditional(it) },
                address = fromDomain(tenderer.address),
                contactPoint = fromDomain(tenderer.contactPoint),
                persones = tenderer.persones!!.map { fromDomain(it) },
            )

        fun fromDomain(identifier: Identifier): Party.Identifier =
            Party.Identifier(
                scheme = identifier.scheme,
                legalName = identifier.legalName,
                id = identifier.id,
                uri = identifier.uri
            )

        fun fromDomainAdditional(additionalIdentifiers: Identifier): Party.AdditionalIdentifier =
            Party.AdditionalIdentifier(
                scheme = additionalIdentifiers.scheme,
                legalName = additionalIdentifiers.legalName,
                id = additionalIdentifiers.id,
                uri = additionalIdentifiers.uri
            )

        fun fromDomain(address: Address): Party.Address =
            Party.Address(
                streetAddress = address.streetAddress,
                postalCode = address.postalCode,
                addressDetails = fromDomain(address.addressDetails)
            )

        fun fromDomain(addresDetails: AddressDetails): Party.Address.AddressDetails =
            Party.Address.AddressDetails(
                country = fromDomain(addresDetails.country),
                region = fromDomain(addresDetails.region),
                locality = fromDomain(addresDetails.locality)
            )

        fun fromDomain(country: CountryDetails): Party.Address.AddressDetails.Country =
            Party.Address.AddressDetails.Country(
                id = country.id,
                description = country.description,
                scheme = country.scheme,
                uri = country.uri
            )

        fun fromDomain(region: RegionDetails): Party.Address.AddressDetails.Region =
            Party.Address.AddressDetails.Region(
                id = region.id,
                description = region.description,
                scheme = region.scheme,
                uri = region.uri
            )

        fun fromDomain(locality: LocalityDetails): Party.Address.AddressDetails.Locality =
            Party.Address.AddressDetails.Locality(
                id = locality.id,
                description = locality.description,
                scheme = locality.scheme,
                uri = locality.uri
            )

        fun fromDomain(contactPoint: ContactPoint): Party.ContactPoint =
            Party.ContactPoint(
                name = contactPoint.name,
                email = contactPoint.email!!,
                telephone = contactPoint.telephone,
                faxNumber = contactPoint.faxNumber,
                url = contactPoint.url
            )

        fun fromDomain(persone: Persone): Party.Persone =
            Party.Persone(
                id = persone.id,
                title = persone.title,
                name = persone.name,
                identifier = fromDomainPersonIdentifier(persone.identifier),
                businessFunctions = persone.businessFunctions.map { fromDomain(it) }
            )

        fun fromDomainPersonIdentifier(identifier: Persone.Identifier): Party.Persone.Identifier =
            Party.Persone.Identifier(
                scheme = identifier.scheme,
                id = identifier.id,
                uri = identifier.uri
            )

        fun fromDomain(businessFunction: BusinessFunction): Party.Persone.BusinessFunction =
            Party.Persone.BusinessFunction(
                id = businessFunction.id,
                type = businessFunction.type.key,
                jobTitle = businessFunction.jobTitle,
                period = businessFunction.period.let { period ->
                    Party.Persone.BusinessFunction.Period(
                        startDate = period.startDate
                    )
                },
                documents = businessFunction.documents?.map { fromDomain(it) }
            )

        fun fromDomain(document: BusinessFunction.Document) =
            Party.Persone.BusinessFunction.Document(
                id = document.id,
                documentType = document.documentType.key,
                title = document.title,
                description = document.description
            )
    }
}