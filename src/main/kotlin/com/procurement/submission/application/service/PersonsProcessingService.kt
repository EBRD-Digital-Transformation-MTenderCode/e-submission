package com.procurement.submission.application.service

import com.procurement.submission.application.params.PersonesProcessingParams
import com.procurement.submission.application.params.errors.PersonsProcessingErrors
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.response.PersonesProcessingResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.BusinessFunction
import com.procurement.submission.model.dto.ocds.Persone
import org.springframework.stereotype.Service
import java.util.UUID

interface PersonsProcessingService {
    fun personsProcessing(params: PersonesProcessingParams): Result<PersonesProcessingResult, Fail>
}

@Service
class PersonsProcessingServiceImpl(
    private val bidRepository: BidRepository,
    private val transform: Transform,
) : PersonsProcessingService {
    override fun personsProcessing(params: PersonesProcessingParams): Result<PersonesProcessingResult, Fail> {
        val bidEntities = bidRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .associateBy { it.bidId }


        if (bidEntities.isEmpty())
            return PersonsProcessingErrors.BidsNotFound(params.cpid, params.ocid).asFailure()

        val bids = bidEntities.values
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val receivedOrganizationId = params.parties.first().id

        val bid = bids.firstOrNull { bid ->
            receivedOrganizationId in bid.tenderers.toSetBy { it.id }
        } ?: return PersonsProcessingErrors.OrganizationNotFound(receivedOrganizationId).asFailure()

        val organization = bid.tenderers.firstOrNull { organization ->
            organization.id == receivedOrganizationId
        }!!

        val receivedPersonsById = params.parties.first().persones.associateBy { it.id }

        val updatedPersons = organization.persones
            ?.map { persone ->
                if (persone.id in receivedPersonsById) {
                    val receivedPerson = receivedPersonsById.getValue(persone.id)
                    persone.copy(
                        title = receivedPerson.title.key,
                        name = receivedPerson.name,
                        identifier = persone.identifier.copy(
                            uri = receivedPerson.identifier.uri
                        ),
                        businessFunctions = updateBusinessFunctions(persone, receivedPerson)
                    )
                } else persone
            }
            ?: emptyList()

        val personIds = organization.persones?.toSetBy { it.id } ?: emptySet()

        val createdPersons = receivedPersonsById.values
            .filter { persone ->
                persone.id !in personIds
            }
            .map { persone ->
                Persone(
                    id = persone.id,
                    title = persone.title.key,
                    name = persone.name,
                    identifier = persone.identifier
                        .let { identifier ->
                            Persone.Identifier(
                                id = identifier.id,
                                scheme = identifier.scheme,
                                uri = identifier.uri
                            )
                        },
                    businessFunctions = persone.businessFunctions
                        .map { businessFunction ->
                            BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = businessFunction.period
                                    .let { period ->
                                        BusinessFunction.Period(
                                            startDate = period.startDate
                                        )
                                    },
                                documents = businessFunction.documents
                                    .map { document ->
                                        BusinessFunction.Document(
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

        val updatedOrganization = organization.copy(persones = updatedPersons + createdPersons)

        val updatedTenderers = bid.tenderers
            .map { tenderer ->
                if (tenderer.id == updatedOrganization.id) updatedOrganization else tenderer
            }

        val updatedBid = bid.copy(tenderers = updatedTenderers)

        val entity = bidEntities.getValue(UUID.fromString(updatedBid.id))

        val updatedEntity: BidEntity = BidEntity.Updated(
            cpid = entity.cpid,
            ocid = entity.ocid,
            createdDate = entity.createdDate,
            pendingDate = entity.pendingDate,
            bid = updatedBid
        )

        bidRepository.save(updatedEntity)

        return updatedBid.tenderers
            .map { PersonesProcessingResult.ResponseConverter.fromDomain(it) }
            .let { PersonesProcessingResult(it) }
            .asSuccess()
    }

    private fun updateBusinessFunctions(
        persone: Persone,
        receivedPerson: PersonesProcessingParams.Party.Persone
    ): List<BusinessFunction> {
        val receivedBusinessFunction = receivedPerson.businessFunctions.associateBy { it.id }
        val updatedBusinessFunction = persone.businessFunctions.map { businessFunction ->
            if (businessFunction.id in receivedBusinessFunction) {
                val receivedFunction = receivedBusinessFunction.getValue(businessFunction.id)
                businessFunction.copy(
                    type = receivedFunction.type,
                    jobTitle = receivedFunction.jobTitle,
                    period = businessFunction.period.copy(
                        startDate = receivedFunction.period.startDate
                    ),
                    documents = updateDocuments(businessFunction, receivedFunction)
                )
            } else businessFunction
        }

        val businessFunctionIds = persone.businessFunctions.toSetBy { it.id }

        val createdBusinessFunctions = receivedBusinessFunction.values
            .filter { businessFunction ->
                businessFunction.id !in businessFunctionIds
            }
            .map { businessFunction ->
                BusinessFunction(
                    id = businessFunction.id,
                    type = businessFunction.type,
                    jobTitle = businessFunction.jobTitle,
                    period = businessFunction.period.let { period ->
                        BusinessFunction.Period(
                            startDate = period.startDate
                        )
                    },
                    documents = businessFunction.documents.map { document ->
                        BusinessFunction.Document(
                            id = document.id,
                            description = document.description,
                            documentType = document.documentType,
                            title = document.title
                        )
                    }
                )
            }
        return createdBusinessFunctions + updatedBusinessFunction
    }

    private fun updateDocuments(
        businessFunction: BusinessFunction,
        receivedFunction: PersonesProcessingParams.Party.Persone.BusinessFunction
    ): List<BusinessFunction.Document> {
        val receivedDocuments = receivedFunction.documents.associateBy { it.id }

        val documents = businessFunction.documents.orEmpty()

        val updatedDocuments = documents
            .map { document ->
                if (document.id in receivedDocuments) {
                    val receivedDocument = receivedDocuments.getValue(document.id)
                    document.copy(
                        documentType = receivedDocument.documentType,
                        description = receivedDocument.description,
                        title = receivedDocument.title
                    )
                } else document
            }

        val documentsIds: Set<String> = documents.toSetBy { it.id }

        val createdDocuments = receivedFunction.documents
            .filter { document ->
                document.id !in documentsIds
            }
            .map { document ->
                BusinessFunction.Document(
                    id = document.id,
                    documentType = document.documentType,
                    title = document.title,
                    description = document.description
                )
            }

        return updatedDocuments + createdDocuments
    }
}


