package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.exception.ErrorType.BID_ALREADY_WITH_LOT
import com.procurement.submission.application.exception.ErrorType.ENTITY_NOT_FOUND
import com.procurement.submission.application.exception.ErrorType.INVALID_AMOUNT
import com.procurement.submission.application.exception.ErrorType.INVALID_CURRENCY
import com.procurement.submission.application.exception.ErrorType.INVALID_DATE
import com.procurement.submission.application.exception.ErrorType.INVALID_DOCS_FOR_UPDATE
import com.procurement.submission.application.exception.ErrorType.INVALID_DOCS_ID
import com.procurement.submission.application.exception.ErrorType.INVALID_OWNER
import com.procurement.submission.application.exception.ErrorType.INVALID_PERSONES
import com.procurement.submission.application.exception.ErrorType.INVALID_RELATED_LOT
import com.procurement.submission.application.exception.ErrorType.INVALID_STATUSES_FOR_UPDATE
import com.procurement.submission.application.exception.ErrorType.INVALID_TENDERER
import com.procurement.submission.application.exception.ErrorType.INVALID_TOKEN
import com.procurement.submission.application.exception.ErrorType.NOT_UNIQUE_IDS
import com.procurement.submission.application.exception.ErrorType.PERIOD_NOT_EXPIRED
import com.procurement.submission.application.exception.ErrorType.RELATED_LOTS_MUST_BE_ONE_UNIT
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsContext
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsData
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsResult
import com.procurement.submission.application.model.data.bid.create.BidCreateContext
import com.procurement.submission.application.model.data.bid.create.BidCreateData
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsContext
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsData
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsResult
import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationRequestData
import com.procurement.submission.application.model.data.bid.get.BidsForEvaluationResponseData
import com.procurement.submission.application.model.data.bid.get.GetBidsForEvaluationContext
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsContext
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsData
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsResult
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingContext
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingData
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingResult
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsContext
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsData
import com.procurement.submission.application.model.data.bid.status.FinalizedBidsStatusByLots
import com.procurement.submission.application.model.data.bid.update.BidUpdateContext
import com.procurement.submission.application.model.data.bid.update.BidUpdateData
import com.procurement.submission.application.params.bid.CreateBidParams
import com.procurement.submission.application.params.bid.ValidateBidDataParams
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.extension.getDuplicate
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.ProcurementMethodModalities
import com.procurement.submission.domain.model.enums.Scale
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.domain.model.enums.TypeOfSupplier
import com.procurement.submission.domain.model.isNotUniqueIds
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.infrastructure.api.v1.CommandMessage
import com.procurement.submission.infrastructure.api.v1.ResponseDto
import com.procurement.submission.infrastructure.api.v1.cpid
import com.procurement.submission.infrastructure.api.v1.ctxId
import com.procurement.submission.infrastructure.api.v1.ocid
import com.procurement.submission.infrastructure.api.v1.owner
import com.procurement.submission.infrastructure.api.v1.startDate
import com.procurement.submission.infrastructure.api.v1.token
import com.procurement.submission.infrastructure.handler.v1.converter.convert
import com.procurement.submission.infrastructure.handler.v1.converter.toBidsForEvaluationResponseData
import com.procurement.submission.infrastructure.handler.v1.model.request.BidUpdateDocsRq
import com.procurement.submission.infrastructure.handler.v1.model.response.BidCreateResponse
import com.procurement.submission.infrastructure.handler.v1.model.response.BidRs
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.converter.convertToCreateBidResult
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateBidResult
import com.procurement.submission.lib.errorIfBlank
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.model.dto.ocds.AccountIdentification
import com.procurement.submission.model.dto.ocds.AdditionalAccountIdentifier
import com.procurement.submission.model.dto.ocds.Address
import com.procurement.submission.model.dto.ocds.AddressDetails
import com.procurement.submission.model.dto.ocds.BankAccount
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.BusinessFunction
import com.procurement.submission.model.dto.ocds.ContactPoint
import com.procurement.submission.model.dto.ocds.CountryDetails
import com.procurement.submission.model.dto.ocds.Details
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Identifier
import com.procurement.submission.model.dto.ocds.IssuedBy
import com.procurement.submission.model.dto.ocds.IssuedThought
import com.procurement.submission.model.dto.ocds.LegalForm
import com.procurement.submission.model.dto.ocds.LocalityDetails
import com.procurement.submission.model.dto.ocds.MainEconomicActivity
import com.procurement.submission.model.dto.ocds.Organization
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.ocds.Permit
import com.procurement.submission.model.dto.ocds.PermitDetails
import com.procurement.submission.model.dto.ocds.PersonId
import com.procurement.submission.model.dto.ocds.Persone
import com.procurement.submission.model.dto.ocds.RegionDetails
import com.procurement.submission.model.dto.ocds.Requirement
import com.procurement.submission.model.dto.ocds.RequirementResponse
import com.procurement.submission.model.dto.ocds.ValidityPeriod
import com.procurement.submission.utils.containsAny
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class BidService(
    private val generationService: GenerationService,
    private val rulesService: RulesService,
    private val periodService: PeriodService,
    private val bidRepository: BidRepository,
    private val invitationRepository: InvitationRepository,
    private val transform: Transform,
) {

    fun createBid(requestData: BidCreateData, context: BidCreateContext): ResponseDto {
        requestData.validateTextAttributes()
        requestData.validateDuplicates()

        val bidRequest = requestData.bid
        periodService.checkCurrentDateInPeriod(context.cpid, context.ocid, context.startDate)
        checkRelatedLotsInDocuments(bidRequest)
        isOneRelatedLot(bidRequest)
        checkTypeOfDocumentsCreateBid(bidRequest.documents)
        checkTenderers(context.cpid, context.ocid, bidRequest)
        checkDocumentsIds(bidRequest.documents)
        checkMoney(bidRequest.value)                                // FReq-1.2.1.42
        checkCurrency(bidRequest.value, requestData.lot.value)      // FReq-1.2.1.43
        checkEntitiesListUniquenessById(bid = bidRequest)           // FReq-1.2.1.6
        checkBusinessFunctionTypeOfDocumentsCreateBid(bidRequest)   // FReq-1.2.1.19
        checkOneAuthority(bid = bidRequest)                         // FReq-1.2.1.20
        checkBusinessFunctionsPeriod(bidRequest, context.startDate) // FReq-1.2.1.39
        checkTenderersInvitations(
            context.cpid,
            context.pmd,
            bidRequest.tenderers,
            ::getInvitedTenderers
        ) // FReq-1.2.1.46

        val requirementResponses = requirementResponseIdTempToPermanent(bidRequest.requirementResponses)

        val bid = Bid(
            id = generationService.generateBidId().toString(),
            date = context.startDate,
            status = Status.PENDING,
            statusDetails = StatusDetails.EMPTY,
            value = bidRequest.value,
            documents = bidRequest.documents.toBidEntityDocuments(),
            relatedLots = bidRequest.relatedLots,
            tenderers = bidRequest.tenderers.toBidEntityTenderers(),
            requirementResponses = requirementResponses.toBidEntityRequirementResponse()
        )
        val entity = BidEntity.New(
            cpid = context.cpid,
            ocid = context.ocid,
            owner = context.owner,
            token = generationService.generateRandomUUID(),
            createdDate = context.startDate,
            pendingDate = context.startDate,
            bid = bid
        )
        bidRepository.save(entity)
        val bidResponse = BidCreateResponse.Bid(
            id = BidId.fromString(bid.id),
            token = entity.token
        )
        return ResponseDto(data = BidCreateResponse(bid = bidResponse))
    }

    fun updateBid(requestData: BidUpdateData, context: BidUpdateContext): ResponseDto {
        requestData.validateTextAttributes()
        requestData.validateDuplicates()

        val bidId = context.id
        val bidRequest = requestData.bid

        periodService.checkCurrentDateInPeriod(context.cpid, context.ocid, context.startDate)

        val entity = bidRepository.findBy(context.cpid, context.ocid, BidId.fromString(bidId))
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        if (entity.token != context.token) throw ErrorException(INVALID_TOKEN)
        if (entity.owner != context.owner) throw ErrorException(INVALID_OWNER)

        val bid: Bid = toObject(Bid::class.java, entity.jsonData)

        checkStatusesBidUpdate(bid)
        checkTypeOfDocumentsUpdateBid(bidRequest.documents)
        validateRelatedLotsOfDocuments(bidDto = bidRequest, bidEntity = bid)
        checkEntitiesListUniquenessById(bid = bidRequest)                               // Freq-1.2.1.6
        checkBusinessFunctionTypeOfDocumentsUpdateBid(bidRequest)                       // FReq-1.2.1.19
        checkBusinessFunctionsPeriod(bid = bidRequest, requestDate = context.startDate) // FReq-1.2.1.39
        checkRelatedLots(bid, bidRequest)                                               // FReq-1.2.1.41
        checkMoney(bidRequest.value)                                                    // FReq-1.2.1.42
        checkCurrency(bidRequest.value, requestData.lot.value)                          // FReq-1.2.1.43

        val updatedTenderers = updateTenderers(bidRequest, bid)  // FReq-1.2.1.30
        val updatedRequirementResponse = updateRequirementResponse(bidRequest, bid)  // FReq-1.2.1.34
        checkOneAuthority(updatedTenderers)                                             // FReq-1.2.1.26

        val updatedBid = bid.copy(
            date = context.startDate,
            status = Status.PENDING,
            documents = updateDocuments(bid.documents, bidRequest.documents),
            value = bidRequest.value,
            tenderers = updatedTenderers,
            requirementResponses = updatedRequirementResponse
        )

        val updatedBidEntity = BidEntity.Updated(
            cpid = entity.cpid,
            ocid = entity.ocid,
            createdDate = entity.createdDate,
            pendingDate = context.startDate,
            bid = updatedBid
        )

        bidRepository.save(updatedBidEntity)
        return ResponseDto(data = "ok")
    }

    private fun BidCreateData.validateTextAttributes() {

        bid.apply {
            tenderers.forEachIndexed { tendererIdx, tenderer ->
                tenderer.apply {
                    name.checkForBlank("bid.tenderers[$tendererIdx].name")

                    identifier.apply {
                        id.checkForBlank("bid.tenderers[$tendererIdx].identifier.id")
                        legalName.checkForBlank("bid.tenderers[$tendererIdx].identifier.legalName")
                        uri.checkForBlank("bid.tenderers[$tendererIdx].identifier.uri")
                    }

                    additionalIdentifiers.forEachIndexed { additionalIdentifierIdx, additionalIdentifier ->
                        additionalIdentifier.scheme.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].scheme")
                        additionalIdentifier.id.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].id")
                        additionalIdentifier.legalName.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].legalName")
                        additionalIdentifier.uri.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].uri")
                    }

                    address.apply {
                        streetAddress.checkForBlank("bid.tenderers[$tendererIdx].address.streetAddress")
                        postalCode.checkForBlank("bid.tenderers[$tendererIdx].address.postalCode")
                        addressDetails.locality.scheme.checkForBlank("bid.tenderers[$tendererIdx].address.addressDetails.locality.scheme")
                        addressDetails.locality.id.checkForBlank("bid.tenderers[$tendererIdx].address.addressDetails.locality.id")
                        addressDetails.locality.description.checkForBlank("bid.tenderers[$tendererIdx].address.addressDetails.locality.description")
                        addressDetails.locality.uri.checkForBlank("bid.tenderers[$tendererIdx].address.addressDetails.locality.uri")
                    }

                    contactPoint.apply {
                        name.checkForBlank("bid.tenderers[$tendererIdx].contactPoint.name")
                        email.checkForBlank("bid.tenderers[$tendererIdx].contactPoint.email")
                        telephone.checkForBlank("bid.tenderers[$tendererIdx].contactPoint.telephone")
                        faxNumber.checkForBlank("bid.tenderers[$tendererIdx].contactPoint.faxNumber")
                        url.checkForBlank("bid.tenderers[$tendererIdx].contactPoint.url")
                    }

                    persones.forEachIndexed { personIdx, person ->
                        person.title.checkForBlank("tenderer.persones[$personIdx].title")
                        person.name.checkForBlank("tenderer.persones[$personIdx].name")
                        person.identifier.scheme.checkForBlank("tenderer.persones[$personIdx].identifier.scheme")
                        person.identifier.id.checkForBlank("tenderer.persones[$personIdx].identifier.id")
                        person.identifier.uri.checkForBlank("tenderer.persones[$personIdx].identifier.uri")

                        person.businessFunctions
                            .forEachIndexed { businessFunctionIdx, businessFunction ->
                                businessFunction.id.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].id")
                                businessFunction.jobTitle.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].jobTitle")

                                businessFunction.documents
                                    .forEachIndexed { documentIdx, document ->
                                        document.title.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].title")
                                        document.description.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].description")
                                    }
                            }
                    }

                    details.apply {
                        scale.checkForBlank("tenderer.details.scale")
                        mainEconomicActivities.forEachIndexed { mainEconomicActivityIdx, mainEconomicActivity ->
                            mainEconomicActivity.scheme.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].scheme")
                            mainEconomicActivity.id.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].id")
                            mainEconomicActivity.description.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].description")
                            mainEconomicActivity.uri.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].uri")
                        }

                        permits.forEachIndexed { permitIdx, permit ->
                            permit.scheme.checkForBlank("tenderer.details.permits[$permitIdx].scheme")
                            permit.id.checkForBlank("tenderer.details.permits[$permitIdx].id")
                            permit.url.checkForBlank("tenderer.details.permits[$permitIdx].url")

                            permit.permitDetails
                                .apply {
                                    issuedBy.id.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedBy.id")
                                    issuedBy.name.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedBy.name")

                                    issuedThought.id.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedThought.id")
                                    issuedThought.name.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedThought.name")
                                }
                        }

                        bankAccounts.forEachIndexed { bankAccountIdx, bankAccount ->
                            bankAccount.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].description")
                            bankAccount.bankName.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].bankName")

                            bankAccount.address
                                .apply {
                                    streetAddress.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.streetAddress")
                                    postalCode.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.postalCode")

                                    addressDetails.apply {
                                        country.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.scheme")
                                        country.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.id")
                                        country.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.description")
                                        country.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.uri")

                                        region.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.scheme")
                                        region.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.id")
                                        region.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.description")
                                        region.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.uri")

                                        locality.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.scheme")
                                        locality.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.id")
                                        locality.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.description")
                                        locality.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.uri")
                                    }
                                }

                            bankAccount.identifier.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].identifier.scheme")
                            bankAccount.identifier.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].identifier.id")
                            bankAccount.accountIdentification.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].accountIdentification.scheme")
                            bankAccount.accountIdentification.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].accountIdentification.id")

                            bankAccount.additionalAccountIdentifiers
                                .forEachIndexed { additionalAccountIdentifierIdx, additionalAccountIdentifier ->
                                    additionalAccountIdentifier.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].scheme")
                                    additionalAccountIdentifier.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].id")
                                }
                        }

                        legalForm?.apply {
                            scheme.checkForBlank("tenderer.details.legalForm.scheme")
                            id.checkForBlank("tenderer.details.legalForm.id")
                            description.checkForBlank("tenderer.details.legalForm.description")
                            uri.checkForBlank("tenderer.details.legalForm.uri")
                        }
                    }
                }
            }

            documents.forEachIndexed { documentIdx, document ->
                document.title.checkForBlank("bid.documents[$documentIdx].title")
                document.description.checkForBlank("bid.documents[$documentIdx].description")
            }

            requirementResponses.forEachIndexed { requirementResponseIdx, requirementResponse ->
                requirementResponse.title.checkForBlank("bid.requirementResponses[$requirementResponseIdx].title")
                requirementResponse.description.checkForBlank("bid.requirementResponses[$requirementResponseIdx].description")
            }
        }
    }

    private fun BidUpdateData.validateTextAttributes() {

        bid.apply {
            tenderers.forEachIndexed { tendererIdx, tenderer ->
                tenderer.apply {
                    additionalIdentifiers.forEachIndexed { additionalIdentifierIdx, additionalIdentifier ->
                        additionalIdentifier.scheme.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].scheme")
                        additionalIdentifier.id.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].id")
                        additionalIdentifier.legalName.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].legalName")
                        additionalIdentifier.uri.checkForBlank("bid.tenderers[$tendererIdx].additionalIdentifiers[$additionalIdentifierIdx].uri")
                    }

                    persones.forEachIndexed { personIdx, person ->
                        person.title.checkForBlank("tenderer.persones[$personIdx].title")
                        person.name.checkForBlank("tenderer.persones[$personIdx].name")
                        person.identifier.scheme.checkForBlank("tenderer.persones[$personIdx].identifier.scheme")
                        person.identifier.id.checkForBlank("tenderer.persones[$personIdx].identifier.id")
                        person.identifier.uri.checkForBlank("tenderer.persones[$personIdx].identifier.uri")

                        person.businessFunctions
                            .forEachIndexed { businessFunctionIdx, businessFunction ->
                                businessFunction.id.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].id")
                                businessFunction.jobTitle.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].jobTitle")

                                businessFunction.documents
                                    .forEachIndexed { documentIdx, document ->
                                        document.title.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].title")
                                        document.description.checkForBlank("tenderer.persones[$personIdx].businessFunctions[$businessFunctionIdx].documents[$documentIdx].description")
                                    }
                            }
                    }

                    details?.apply {

                        mainEconomicActivities.forEachIndexed { mainEconomicActivityIdx, mainEconomicActivity ->
                            mainEconomicActivity.scheme.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].scheme")
                            mainEconomicActivity.id.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].id")
                            mainEconomicActivity.description.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].description")
                            mainEconomicActivity.uri.checkForBlank("tenderer.details.mainEconomicActivities[$mainEconomicActivityIdx].uri")
                        }

                        permits.forEachIndexed { permitIdx, permit ->
                            permit.scheme.checkForBlank("tenderer.details.permits[$permitIdx].scheme")
                            permit.id.checkForBlank("tenderer.details.permits[$permitIdx].id")
                            permit.url.checkForBlank("tenderer.details.permits[$permitIdx].url")

                            permit.permitDetails
                                .apply {
                                    issuedBy.id.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedBy.id")
                                    issuedBy.name.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedBy.name")

                                    issuedThought.id.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedThought.id")
                                    issuedThought.name.checkForBlank("tenderer.details.permits[$permitIdx].permitDetails.issuedThought.name")
                                }
                        }

                        bankAccounts.forEachIndexed { bankAccountIdx, bankAccount ->
                            bankAccount.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].description")
                            bankAccount.bankName.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].bankName")

                            bankAccount.address
                                .apply {
                                    streetAddress.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.streetAddress")
                                    postalCode.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.postalCode")

                                    addressDetails.apply {
                                        country.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.scheme")
                                        country.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.id")
                                        country.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.description")
                                        country.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.country.uri")

                                        region.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.scheme")
                                        region.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.id")
                                        region.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.description")
                                        region.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.region.uri")

                                        locality.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.scheme")
                                        locality.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.id")
                                        locality.description.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.description")
                                        locality.uri.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].address.addressDetails.locality.uri")
                                    }
                                }

                            bankAccount.identifier.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].identifier.scheme")
                            bankAccount.identifier.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].identifier.id")
                            bankAccount.accountIdentification.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].accountIdentification.scheme")
                            bankAccount.accountIdentification.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].accountIdentification.id")

                            bankAccount.additionalAccountIdentifiers
                                .forEachIndexed { additionalAccountIdentifierIdx, additionalAccountIdentifier ->
                                    additionalAccountIdentifier.scheme.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].scheme")
                                    additionalAccountIdentifier.id.checkForBlank("tenderer.details.bankAccounts[$bankAccountIdx].additionalAccountIdentifiers[$additionalAccountIdentifierIdx].id")
                                }
                        }

                        legalForm?.apply {
                            scheme.checkForBlank("tenderer.details.legalForm.scheme")
                            id.checkForBlank("tenderer.details.legalForm.id")
                            description.checkForBlank("tenderer.details.legalForm.description")
                            uri.checkForBlank("tenderer.details.legalForm.uri")
                        }
                    }
                }
            }

            documents.forEachIndexed { documentIdx, document ->
                document.title.checkForBlank("bid.documents[$documentIdx].title")
                document.description.checkForBlank("bid.documents[$documentIdx].description")
            }

            requirementResponses.forEachIndexed { requirementResponseIdx, requirementResponse ->
                requirementResponse.id.checkForBlank("bid.requirementResponses[$requirementResponseIdx].id")
                requirementResponse.title.checkForBlank("bid.requirementResponses[$requirementResponseIdx].title")
                requirementResponse.description.checkForBlank("bid.requirementResponses[$requirementResponseIdx].description")
            }
        }
    }

    private fun String?.checkForBlank(name: String) = this.errorIfBlank {
        ErrorException(
            error = ErrorType.INCORRECT_VALUE_ATTRIBUTE,
            message = "The attribute '$name' is empty or blank."
        )
    }

    private fun BidCreateData.validateDuplicates() {
        bid.tenderers
            .forEachIndexed { tendererIdx, tenderer ->
                val duplicate =
                    tenderer.details.mainEconomicActivities.getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'bid.tenderers[$tendererIdx].details.mainEconomicActivities' has duplicate by scheme '${duplicate.scheme}' and id '${duplicate.id}'."
                    )
            }

        bid.documents
            .forEach { document ->
                val duplicate = document.relatedLots.getDuplicate { it }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'bid.documents.relatedLots' has duplicate '$duplicate'."
                    )
            }
    }

    private fun BidUpdateData.validateDuplicates() {
        bid.tenderers
            .forEachIndexed { tendererIdx, tenderer ->
                val duplicate =
                    tenderer.details?.mainEconomicActivities.getDuplicate { it.scheme.toUpperCase() + it.id.toUpperCase() }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'bid.tenderers[$tendererIdx].details.mainEconomicActivities' has duplicate by scheme '${duplicate.scheme}' and id '${duplicate.id}'."
                    )
            }

        bid.documents
            .forEach { document ->
                val duplicate = document.relatedLots.getDuplicate { it }
                if (duplicate != null)
                    throw ErrorException(
                        error = ErrorType.DUPLICATE,
                        message = "Attribute 'bid.documents.relatedLots' has duplicate '$duplicate'."
                    )
            }
    }

    fun getBidsForEvaluation(
        requestData: BidsForEvaluationRequestData,
        context: GetBidsForEvaluationContext
    ): BidsForEvaluationResponseData {
        val bidsEntitiesByIds = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == Status.PENDING }
            .associateBy { it.bidId }

        val bidsDb = bidsEntitiesByIds.asSequence()
            .map { (id, entity) ->
                id to toObject(Bid::class.java, entity.jsonData)
            }
            .toMap()

        val bidsByRelatedLot: Map<String, List<Bid>> = bidsDb.values
            .asSequence()
            .flatMap { bid ->
                bid.relatedLots
                    .asSequence()
                    .map { lotId -> lotId to bid }
            }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

        val minNumberOfBids = rulesService.getRulesMinBids(context.country, context.pmd)
        val bidsForEvaluation = requestData.lots
            .asSequence()
            .flatMap { lot ->
                val bids = bidsByRelatedLot[lot.id.toString()] ?: emptyList()
                if (bids.size >= minNumberOfBids)
                    bids.asSequence()
                else
                    emptySequence()
            }
            .associateBy { bid -> UUID.fromString(bid.id) }

        val updatedBidEntities = getBidsForArchive(bids = bidsDb, subtractBids = bidsForEvaluation)
            .asSequence()
            .map { bid -> bid.archive() }
            .map { updatedBid ->
                val entity = bidsEntitiesByIds.getValue(BidId.fromString(updatedBid.id))
                BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = updatedBid
                )
            }
            .toList()
        bidRepository.save(updatedBidEntities)

        return bidsForEvaluation.values.toBidsForEvaluationResponseData()
    }

    private fun getBidsForArchive(bids: Map<UUID, Bid>, subtractBids: Map<UUID, Bid>) =
        bids.asSequence()
            .filter { (id, _) -> id !in subtractBids }
            .map { it.value }

    private fun Bid.archive() = this.copy(statusDetails = StatusDetails.ARCHIVED)

    fun openBidsForPublishing(
        context: OpenBidsForPublishingContext,
        data: OpenBidsForPublishingData
    ): OpenBidsForPublishingResult {
        val activeBids: List<Bid> = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == Status.PENDING }
            .map { bidRecord -> toObject(Bid::class.java, bidRecord.jsonData) }
            .filter { it.statusDetails == StatusDetails.EMPTY }
            .toList()

        val bidsForPublishing = when (data.awardCriteriaDetails) {
            AwardCriteriaDetails.AUTOMATED -> {
                val relatedBids: Set<BidId> = data.awards
                    .asSequence()
                    .filter { it.relatedBid != null }
                    .map { it.relatedBid!! }
                    .toSet()

                activeBids.asSequence()
                    .filter { bid -> BidId.fromString(bid.id) in relatedBids }
                    .map { bid ->
                        val bidForPublishing = bid.copy(
                            documents = bid.documents
                                ?.filter { document ->
                                    document.documentType == DocumentType.SUBMISSION_DOCUMENTS
                                        || document.documentType == DocumentType.ELIGIBILITY_DOCUMENTS
                                }
                        )
                        bidForPublishing.convert()
                    }
                    .toList()
            }

            AwardCriteriaDetails.MANUAL -> {
                activeBids.map { bid -> bid.convert() }
            }
        }
        return OpenBidsForPublishingResult(
            bids = bidsForPublishing
        )
    }

    fun updateBidDocs(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner
        val bidId = cm.ctxId
        val dateTime = cm.startDate
        val dto = toObject(BidUpdateDocsRq::class.java, cm.data)
        val documentsDto = dto.bid.documents
        //VR-4.8.1
        val period = periodService.getPeriodEntity(cpid, ocid)
        if (dateTime <= period.endDate) throw ErrorException(PERIOD_NOT_EXPIRED)

        val entity = bidRepository.findBy(cpid, ocid, BidId.fromString(bidId))
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        if (entity.token != token) throw ErrorException(INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        //VR-4.8.4
        if ((bid.status != Status.PENDING && bid.statusDetails != StatusDetails.VALID)
            && (bid.status != Status.VALID && bid.statusDetails != StatusDetails.EMPTY)
        ) {
            throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        }
        //VR-4.8.5
        documentsDto.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots!!)) throw ErrorException(INVALID_RELATED_LOT)
            }
        }
        //BR-4.8.2
        val documentsDtoId = documentsDto.toSetBy { it.id }
        val documentsDbId = bid.documents?.toSetBy { it.id } ?: setOf()
        val newDocumentsId = documentsDtoId - documentsDbId
        if (newDocumentsId.isEmpty()) throw ErrorException(INVALID_DOCS_FOR_UPDATE)
        val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
        val documentsDb = bid.documents ?: listOf()
        bid.documents = documentsDb + newDocuments

        val updatedBidEntity = BidEntity.Updated(
            cpid = entity.cpid,
            ocid = entity.ocid,
            createdDate = entity.createdDate,
            pendingDate = entity.pendingDate,
            bid = bid
        )
        bidRepository.save(updatedBidEntity)
        return ResponseDto(data = BidRs(null, null, bid))
    }

    /**
     * BR-4.6.6 "status" "statusDetails" (Bid) (set final status by lots)
     *
     * 1. Finds all Bids objects in DB by values of Stage && CPID from the context of Request and saves them as a list to memory;
     * 2. FOR every lot.ID value from list got in Request, eSubmission executes next steps:
     *   a. Selects bids from list (got on step 1) where bid.relatedLots == lots.[id] and saves them as a list to memory;
     *   b. Selects bids from list (got on step 2.a) with bid.status == "pending" && bid.statusDetails == "disqualified" and saves them as a list to memory;
     *   c. FOR every bid from list got on step 2.b:
     *     i.   Sets bid.status == "disqualified" && bid.statusDetails ==  "empty";
     *     ii.  Saves updated Bid to DB;
     *     iii. Returns it for Response as bid.ID && bid.status && bid.statusDetails;
     *   d. Selects bids from list (got on step 2.a) with bid.status == "pending" && bid.statusDetails == "valid" and saves them as a list to memory;
     *   e. FOR every bid from list got on step 2.d:
     *     i.   Sets bid.status == "valid" && bid.statusDetails ==  "empty";
     *     ii.  Saves updated bid to DB;
     *     iii. Returns it for Response as bid.ID && bid.status && bid.statusDetails;
     */
    fun finalBidsStatusByLots(
        context: FinalBidsStatusByLotsContext,
        data: FinalBidsStatusByLotsData
    ): FinalizedBidsStatusByLots {
        fun isValid(status: Status, details: StatusDetails) =
            status == Status.PENDING && details == StatusDetails.VALID

        fun isDisqualified(status: Status, details: StatusDetails) =
            status == Status.PENDING && details == StatusDetails.DISQUALIFIED

        fun predicateOfBidStatus(bid: Bid): Boolean = isValid(status = bid.status, details = bid.statusDetails)
            || isDisqualified(status = bid.status, details = bid.statusDetails)

        fun Bid.updatingStatuses(): Bid = when {
            isValid(this.status, this.statusDetails) -> this.copy(
                status = Status.VALID,
                statusDetails = StatusDetails.EMPTY
            )
            isDisqualified(this.status, this.statusDetails) -> this.copy(
                status = Status.DISQUALIFIED,
                statusDetails = StatusDetails.EMPTY
            )
            else -> throw IllegalStateException("No processing for award with status: '${this.status}' and details: '${this.statusDetails}'.")
        }

        val lotsIds: Set<LotId> = data.lots.toSetBy { it.id }

        val updatedBids: Map<Bid, BidEntity.Updated> = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .map { entity ->
                val bid = toObject(Bid::class.java, entity.jsonData)
                bid to entity
            }
            .filter { (bid, _) ->
                bid.relatedLots.any { lotsIds.contains(LotId.fromString(it)) } && predicateOfBidStatus(bid = bid)
            }
            .map { (bid, entity) ->
                val updatedBid = bid.updatingStatuses()
                val updatedBidEntity = BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = updatedBid
                )
                updatedBid to updatedBidEntity
            }
            .toMap()

        bidRepository.save(updatedBids.values)

        return FinalizedBidsStatusByLots(
            bids = updatedBids.keys
                .map { bid ->
                    FinalizedBidsStatusByLots.Bid(
                        id = UUID.fromString(bid.id),
                        status = bid.status,
                        statusDetails = bid.statusDetails
                    )
                }
        )
    }

    /**
     * CR-10.1.2.1
     *
     * eSubmission executes next steps:
     * 1. forEach award object from Request system executes:
     *   a. Finds appropriate bid object in DB where bid.id == award.relatedBid value from processed award object;
     *   b. Sets bid.statusDetails in object (found before) by rule BR-10.1.2.1;
     *   c. Saves updated Bid to DB;
     *   d. Adds updated Bid to Bids array for response up to next data model:
     *     i.  bid.ID;
     *     ii. bid.statusDetails;
     * 2. Returns bids array for Response;
     *
     */
    fun applyEvaluatedAwards(
        context: ApplyEvaluatedAwardsContext,
        data: ApplyEvaluatedAwardsData
    ): ApplyEvaluatedAwardsResult {
        val relatedBidsByStatuses: Map<UUID, AwardStatusDetails> = data.awards.associate {
            it.relatedBid to it.statusDetails
        }

        val updatedBidEntitiesByBid =
            bidRepository.findBy(context.cpid, context.ocid)
                .orThrow { it.exception }
                .asSequence()
                .filter { entity -> entity.bidId in relatedBidsByStatuses }
                .map { entity ->
                    val statusDetails = relatedBidsByStatuses.getValue(entity.bidId)
                    val updatedBid: Bid = toObject(Bid::class.java, entity.jsonData)
                        .updateStatusDetails(statusDetails)
                    val updatedBidEntity = BidEntity.Updated(
                        cpid = entity.cpid,
                        ocid = entity.ocid,
                        createdDate = entity.createdDate,
                        pendingDate = entity.pendingDate,
                        bid = updatedBid
                    )
                    updatedBid to updatedBidEntity
                }
                .toMap()

        val result = ApplyEvaluatedAwardsResult(
            bids = updatedBidEntitiesByBid.keys
                .map { bid ->
                    ApplyEvaluatedAwardsResult.Bid(
                        id = BidId.fromString(bid.id),
                        statusDetails = bid.statusDetails
                    )
                }
        )

        bidRepository.save(updatedBidEntitiesByBid.values)
        return result
    }

    /**
     * BR-10.1.2.1 statusDetails (bid)
     *
     * 1. eEvaluation determines bid.statusDetails depends on award.statusDetails from processed award object of Request:
     *   a. IF [award.statusDetails == "active"] then:
     *      system sets bid.statusDetails == "valid";
     *   b. ELSE [award.statusDetails == "unsuccessful"] then:
     *      system sets bid.statusDetails == "disqualified";
     */
    private fun Bid.updateStatusDetails(statusDetails: AwardStatusDetails): Bid = when (statusDetails) {
        AwardStatusDetails.ACTIVE -> this.copy(statusDetails = StatusDetails.VALID)
        AwardStatusDetails.UNSUCCESSFUL -> this.copy(statusDetails = StatusDetails.DISQUALIFIED)

        AwardStatusDetails.EMPTY,
        AwardStatusDetails.PENDING,
        AwardStatusDetails.CONSIDERATION,
        AwardStatusDetails.AWAITING,
        AwardStatusDetails.NO_OFFERS_RECEIVED,
        AwardStatusDetails.LOT_CANCELLED -> throw ErrorException(
            error = ErrorType.INVALID_STATUS_DETAILS,
            message = "Current status details: '$statusDetails'. Expected status details: [${AwardStatusDetails.ACTIVE}, ${AwardStatusDetails.UNSUCCESSFUL}]"
        )
    }

    private fun updateDocuments(
        documentsDb: List<Document>?,
        documentsDto: List<BidUpdateData.Bid.Document>
    ): List<Document>? {
        return if (documentsDb != null && documentsDb.isNotEmpty()) {
            if (documentsDto.isNotEmpty()) {
                val documentsDtoId = documentsDto.toSetBy { it.id }
                if (documentsDtoId.size != documentsDto.size) throw ErrorException(INVALID_DOCS_ID)
                val documentsDbId = documentsDb.toSetBy { it.id }
                val newDocumentsId = documentsDtoId - documentsDbId
                //update
                documentsDb.forEach { document ->
                    document.updateDocument(documentsDto.firstOrNull { it.id == document.id })
                }
                //new
                val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
                documentsDb + newDocuments.toDocumentEntity()
            } else {
                documentsDb
            }
        } else {
            documentsDto.toDocumentEntity()
        }
    }

    private fun List<BidUpdateData.Bid.Document>.toDocumentEntity(): List<Document> {
        return this.map { document ->
            Document(
                id = document.id,
                documentType = document.documentType,
                title = document.title,
                description = document.description,
                relatedLots = document.relatedLots
            )
        }
    }

    private fun Document.updateDocument(documentDto: BidUpdateData.Bid.Document?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description ?: this.description
            this.relatedLots = documentDto.relatedLots.let { if (it.isNotEmpty()) it else this.relatedLots }
        }
    }

    private fun checkStatusesBidUpdate(bid: Bid) {
        if (bid.status != Status.PENDING && bid.status != Status.INVITED)
            throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
        if (bid.statusDetails != StatusDetails.EMPTY)
            throw ErrorException(INVALID_STATUSES_FOR_UPDATE)
    }

    private fun checkRelatedLotsInDocuments(bidDto: BidCreateData.Bid) {
        bidDto.documents.forEach { document ->
            if (!bidDto.relatedLots.containsAll(document.relatedLots)) throw ErrorException(INVALID_RELATED_LOT)
        }
    }

    private fun checkTypeOfDocumentsCreateBid(documents: List<BidCreateData.Bid.Document>) {
        documents.forEach { document ->
            when (document.documentType) {
                DocumentType.SUBMISSION_DOCUMENTS,
                DocumentType.ELIGIBILITY_DOCUMENTS,
                DocumentType.ILLUSTRATION,
                DocumentType.COMMERCIAL_OFFER,
                DocumentType.QUALIFICATION_DOCUMENTS,
                DocumentType.TECHNICAL_DOCUMENTS -> Unit
            }
        }
    }

    private fun checkBusinessFunctionTypeOfDocumentsCreateBid(bid: BidCreateData.Bid) {
        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .flatMap { it.documents.asSequence() }
            .forEach { document ->
                when (document.documentType) {
                    BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> Unit
                }
            }
    }

    private fun checkOneAuthority(bid: BidCreateData.Bid) {
        fun BusinessFunctionType.validate() {
            when (this) {
                BusinessFunctionType.AUTHORITY,
                BusinessFunctionType.CONTACT_POINT -> Unit
            }
        }

        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .map { it.type }
            .forEach { it.validate() }


        bid.tenderers.forEach { tenderer ->
            if (tenderer.persones.isNotEmpty()) {
                val authorityPersones = tenderer.persones
                    .flatMap { it.businessFunctions }
                    .filter { it.type == BusinessFunctionType.AUTHORITY }
                    .toList()

                if (authorityPersones.size > 1) {
                    throw ErrorException(
                        error = INVALID_PERSONES,
                        message = "Only one person with one business functions type 'authority' should be added. "
                    )
                }

                if (authorityPersones.isEmpty()) {
                    throw ErrorException(
                        error = INVALID_PERSONES,
                        message = "At least one person with business function type 'authority' should be added. "
                    )
                }
            }
        }
    }

    private fun requirementResponseIdTempToPermanent(requirementResponses: List<BidCreateData.Bid.RequirementResponse>): List<BidCreateData.Bid.RequirementResponse> {
        return requirementResponses.map { requirementResponse ->
            requirementResponse.copy(id = generationService.generateRequirementResponseId().toString())
        }
    }

    private fun checkBusinessFunctionsPeriod(bid: BidCreateData.Bid, requestDate: LocalDateTime) {
        fun BidCreateData.Bid.Tenderer.Persone.BusinessFunction.Period.validate() {
            if (this.startDate > requestDate)
                throw ErrorException(
                    error = INVALID_DATE,
                    message = "Period.startDate specified in  business functions cannot be greater than startDate from request."
                )
        }

        bid.tenderers.flatMap { it.persones }
            .flatMap { it.businessFunctions }
            .map { it.period }
            .forEach { it.validate() }
    }

    fun getInvitedTenderers(cpid: Cpid): Set<String> = invitationRepository
        .findBy(cpid)
        .onFailure {
            throw ErrorException(
                error = ENTITY_NOT_FOUND,
                message = "Cannot found invitations by cpid='${cpid}'"
            )
        }
        .filter { it.status == InvitationStatus.ACTIVE }
        .flatMap { it.tenderers }
        .toSetBy { it.id }

    private fun checkTypeOfDocumentsUpdateBid(documents: List<BidUpdateData.Bid.Document>) {
        documents.forEach { document ->
            when (document.documentType) {
                DocumentType.SUBMISSION_DOCUMENTS,
                DocumentType.ELIGIBILITY_DOCUMENTS,
                DocumentType.ILLUSTRATION,
                DocumentType.COMMERCIAL_OFFER,
                DocumentType.QUALIFICATION_DOCUMENTS,
                DocumentType.TECHNICAL_DOCUMENTS -> Unit
            }
        }
    }

    private fun checkDocumentsIds(documents: List<BidCreateData.Bid.Document>) {
        if (documents.isNotUniqueIds())
            throw ErrorException(
                error = ErrorType.INVALID_DOCS_ID,
                message = "Some documents have the same id."
            )
    }

    private fun checkMoney(money: Money?) {
        money?.let {
            if (money.amount.compareTo(BigDecimal.ZERO) <= 0)
                throw ErrorException(
                    error = INVALID_AMOUNT,
                    message = "Amount cannot be less than 0. Current value = ${money.amount}"
                )
        }
    }

    private fun checkCurrency(bidMoney: Money?, lotMoney: Money) {
        bidMoney?.let {
            if (!bidMoney.currency.equals(lotMoney.currency, true))
                throw ErrorException(
                    error = INVALID_CURRENCY,
                    message = "Currency in bid missmatch with currency in related lot. " +
                        "Bid currency='${bidMoney.currency}', " +
                        "Lot currency='${lotMoney.currency}'. "
                )
        }
    }

    private fun checkEntitiesListUniquenessById(bid: BidCreateData.Bid) {
        bid.tenderers.isNotUniqueIds {
            throw ErrorException(
                error = NOT_UNIQUE_IDS,
                message = "Some bid.tenderers have the same id."
            )
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.additionalIdentifiers.isNotUniqueIds {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.additionalIdentifiers have the same id."
                )
            }
        }


        bid.tenderers.forEach { tenderer ->
            tenderer.details.permits.isNotUniqueIds {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.details.permits have the same id."
                )
            }
        }

        bid.tenderers.forEach { tenderer ->
            val actualIds = tenderer.details.bankAccounts.map { it.identifier.id }
            val uniqueIds = actualIds.toSet()
            if (actualIds.size != uniqueIds.size) {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.details.bankAccounts have the same identifier id."
                )
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.details.bankAccounts.forEach { bankAccount ->
                val actualIds = bankAccount.additionalAccountIdentifiers.map { it.id }
                val uniqueIds = actualIds.toSet()

                if (actualIds.size != uniqueIds.size) {
                    throw ErrorException(
                        error = NOT_UNIQUE_IDS,
                        message = "Some bid.tenderers.details.bankAccounts.additionalAccountIdentifiers have the same id."
                    )
                }
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.persones.forEach { person ->
                person.businessFunctions.isNotUniqueIds {
                    throw ErrorException(
                        error = NOT_UNIQUE_IDS,
                        message = "Some bid.tenderers.persones.businessFunctions have the same id."
                    )
                }
            }
        }


        bid.tenderers.forEach { tenderer ->
            val actualIds = tenderer.persones.map { it.identifier.id }
            val uniqueIds = actualIds.toSet()
            if (actualIds.size != uniqueIds.size) {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.persones have the same identifier id."
                )
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.persones.forEach { person ->
                person.businessFunctions.forEach { businessFunction ->
                    businessFunction.documents.isNotUniqueIds {
                        throw ErrorException(
                            error = INVALID_DOCS_ID,
                            message = "Some bid.tenderers.persones.businessFunctions.documents have the same id."
                        )
                    }
                }
            }
        }

        bid.documents.isNotUniqueIds {
            throw ErrorException(
                error = INVALID_DOCS_ID,
                message = "Some bid.documents have the same id."
            )
        }

        bid.requirementResponses.isNotUniqueIds {
            throw ErrorException(
                error = NOT_UNIQUE_IDS,
                message = "Some bid.requirementResponses have the same id."
            )
        }
    }

    private fun isOneRelatedLot(bidDto: BidCreateData.Bid) {
        if (bidDto.relatedLots.size > 1) throw ErrorException(RELATED_LOTS_MUST_BE_ONE_UNIT)
    }

    private fun validateRelatedLotsOfDocuments(bidDto: BidUpdateData.Bid, bidEntity: Bid) {
        bidDto.documents.forEach { document ->
            if (!bidEntity.relatedLots.containsAll(document.relatedLots)) throw ErrorException(INVALID_RELATED_LOT)
        }
    }

    private fun checkEntitiesListUniquenessById(bid: BidUpdateData.Bid) {
        bid.tenderers.isNotUniqueIds {
            throw ErrorException(
                error = NOT_UNIQUE_IDS,
                message = "Some bid.tenderers have the same id."
            )
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.additionalIdentifiers.isNotUniqueIds {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.additionalIdentifiers have the same id."
                )
            }
        }


        bid.tenderers.forEach { tenderer ->
            tenderer.details?.permits?.isNotUniqueIds {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.details.permits have the same id."
                )
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.details?.let { details ->
                val actualIds = details.bankAccounts.map { it.identifier.id }
                val uniqueIds = actualIds.toSet()
                if (actualIds.size != uniqueIds.size) {
                    throw ErrorException(
                        error = NOT_UNIQUE_IDS,
                        message = "Some bid.tenderers.details.bankAccounts have the same identifier id."
                    )
                }
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.details?.let { details ->
                details.bankAccounts.forEach { bankAccount ->
                    val actualIds = bankAccount.additionalAccountIdentifiers.map { it.id }
                    val uniqueIds = actualIds.toSet()

                    if (actualIds.size != uniqueIds.size) {
                        throw ErrorException(
                            error = NOT_UNIQUE_IDS,
                            message = "Some bid.tenderers.details.bankAccounts.additionalAccountIdentifiers have the same id."
                        )
                    }
                }
            }

        }

        bid.tenderers.forEach { tenderer ->
            tenderer.persones.forEach { person ->
                person.businessFunctions.isNotUniqueIds {
                    throw ErrorException(
                        error = NOT_UNIQUE_IDS,
                        message = "Some bid.tenderers.persones.businessFunctions have the same id."
                    )
                }
            }
        }


        bid.tenderers.forEach { tenderer ->
            val actualIds = tenderer.persones.map { it.identifier.id }
            val uniqueIds = actualIds.toSet()
            if (actualIds.size != uniqueIds.size) {
                throw ErrorException(
                    error = NOT_UNIQUE_IDS,
                    message = "Some bid.tenderers.persones have the same identifier id."
                )
            }
        }

        bid.tenderers.forEach { tenderer ->
            tenderer.persones.forEach { person ->
                person.businessFunctions.forEach { businessFunction ->
                    businessFunction.documents.isNotUniqueIds {
                        throw ErrorException(
                            error = INVALID_DOCS_ID,
                            message = "Some bid.tenderers.persones.businessFunctions.documents have the same id."
                        )
                    }
                }
            }
        }

        bid.documents.isNotUniqueIds {
            throw ErrorException(
                error = INVALID_DOCS_ID,
                message = "Some bid.documents have the same id."
            )
        }

        bid.requirementResponses.isNotUniqueIds {
            throw ErrorException(
                error = NOT_UNIQUE_IDS,
                message = "Some bid.requirementResponses have the same id."
            )
        }
    }

    private fun checkBusinessFunctionTypeOfDocumentsUpdateBid(bid: BidUpdateData.Bid) {
        bid.tenderers.asSequence()
            .flatMap { it.persones.asSequence() }
            .flatMap { it.businessFunctions.asSequence() }
            .flatMap { it.documents.asSequence() }
            .forEach { document ->
                when (document.documentType) {
                    BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> Unit
                }
            }
    }

    private fun checkOneAuthority(tenderers: List<Organization>) {
        fun BusinessFunctionType.validate() {
            when (this) {
                BusinessFunctionType.AUTHORITY,
                BusinessFunctionType.CONTACT_POINT -> Unit
            }
        }


        tenderers.forEach { tenderer ->
            tenderer.persones?.let { persones ->
                persones
                    .flatMap { it.businessFunctions }
                    .map { it.type }
                    .forEach { it.validate() }

                val authorityPersones = persones
                    .flatMap { it.businessFunctions }
                    .filter { it.type == BusinessFunctionType.AUTHORITY }
                    .toList()

                if (authorityPersones.size > 1) {
                    throw ErrorException(
                        error = INVALID_PERSONES,
                        message = "Only one person with one business functions type 'authority' should be added."
                    )
                }

                if (persones.isNotEmpty() && authorityPersones.isEmpty()) {
                    throw ErrorException(
                        error = INVALID_PERSONES,
                        message = "At least one person with business function type 'authority' should be added. "
                    )
                }
            }
        }
    }

    private fun updateTenderers(bidRequest: BidUpdateData.Bid, bidEntity: Bid): List<Organization> {
        if (bidRequest.tenderers.isEmpty()) return bidEntity.tenderers

        val tenderersRequestIds = bidRequest.tenderers.map { it.id.toString() }
        val tenderersEntityIds = bidEntity.tenderers.map { it.id }

        // FReq-1.2.1.40
        if (!tenderersEntityIds.containsAll(tenderersRequestIds)) {
            throw ErrorException(
                error = INVALID_TENDERER,
                message = "List of tenderers from request contains tenderer that is missing in database"
            )
        }

        return bidEntity.tenderers.map { tenderer ->
            val personesEntities = tenderer.persones
            val tendererRequest = bidRequest.tenderers.find { it.id.toString() == tenderer.id }

            val additionalIdentifiersDb = tenderer.additionalIdentifiers

            if (tendererRequest != null) {
                val additionalIdentifiersRequest = tendererRequest.additionalIdentifiers
                val detailsRequest = tendererRequest.details

                tenderer.copy(
                    persones = updatePersones(personesEntities, tendererRequest.persones),
                    additionalIdentifiers = updateAdditionalIdentifiers(
                        additionalIdentifiersDb,
                        additionalIdentifiersRequest
                    ),
                    details = updateDetails(tenderer.details, detailsRequest)
                )
            } else tenderer

        }
    }

    private fun updateRequirementResponse(bidRequest: BidUpdateData.Bid, bidEntity: Bid): List<RequirementResponse>? {
        if (bidRequest.requirementResponses.isEmpty()) return bidEntity.requirementResponses

        return bidRequest.requirementResponses.map { requirementResponse ->
            RequirementResponse(
                id = generationService.generateBidId().toString(),
                title = requirementResponse.title,
                description = requirementResponse.description,
                value = requirementResponse.value,
                requirement = Requirement(
                    id = requirementResponse.requirement.id
                ),
                period = requirementResponse.period?.let { period ->
                    Period(
                        startDate = period.startDate,
                        endDate = period.endDate
                    )
                }
            )
        }
    }

    private fun updatePersones(
        personesEntities: List<Persone>?,
        personesRequest: List<BidUpdateData.Bid.Tenderer.Persone>
    ): List<Persone>? {
        if (personesRequest.isEmpty()) return personesEntities
        val personesDb = personesEntities ?: emptyList()

        val updatedPersones = personesDb.map { personEntity ->
            val personRequest = personesRequest.find { it.identifier.id == personEntity.identifier.id }
            personRequest?.let { personEntity.updatePerson(it) }
        }.filterNotNull()

        val personesDbIds = personesDb.map { it.identifier.id }
        val newPersones = personesRequest.filter { it.identifier.id !in personesDbIds }

        return updatedPersones + newPersones.toBidEntityPersones()
    }

    private fun Persone.updatePerson(personRequest: BidUpdateData.Bid.Tenderer.Persone): Persone {
        return Persone(
            id = this.id,
            title = personRequest.title,
            name = personRequest.name,
            identifier = this.identifier,
            businessFunctions = updateBusinessFunction(this.businessFunctions, personRequest.businessFunctions)
        )
    }

    private fun updateBusinessFunction(
        businessFunctionsDb: List<BusinessFunction>,
        businessFunctionsRequest: List<BidUpdateData.Bid.Tenderer.Persone.BusinessFunction>
    ): List<BusinessFunction> {
        val newBusinessFunctions = businessFunctionsRequest.filter { it.id !in businessFunctionsDb.map { it.id } }
        val updatedBusinessFunctions = businessFunctionsDb.map { businessFunctionDb ->
            if (businessFunctionDb.id in businessFunctionsRequest.map { it.id }) {
                val businessFunctionRequest = businessFunctionsRequest.find { it.id == businessFunctionDb.id }!!
                BusinessFunction(
                    id = businessFunctionDb.id,
                    type = businessFunctionRequest.type,
                    jobTitle = businessFunctionRequest.jobTitle,
                    period = BusinessFunction.Period(
                        startDate = businessFunctionRequest.period.startDate
                    ),
                    documents = updateBusinessFunctionsDocuments(
                        businessFunctionDb.documents,
                        businessFunctionRequest.documents
                    )
                )
            } else {
                businessFunctionDb
            }
        }

        return updatedBusinessFunctions + newBusinessFunctions.toBidEntityBusinessFunction()
    }

    private fun updateBusinessFunctionsDocuments(
        documentsEntities: List<BusinessFunction.Document>?,
        documentsRequest: List<BidUpdateData.Bid.Tenderer.Persone.BusinessFunction.Document>
    ): List<BusinessFunction.Document> {
        val documentsDb = documentsEntities ?: emptyList()
        val documentsDbIds = documentsDb.toSetBy { it.id }
        val newDocuments = documentsRequest.filter { it.id !in documentsDbIds }
        val documentsRequestIds = documentsRequest.toSetBy { it.id }
        val updatedDocuments = documentsDb.map { documentDb ->
            if (documentDb.id in documentsRequestIds) {
                val documentRequest = documentsRequest.find { it.id == documentDb.id }!!

                BusinessFunction.Document(
                    id = documentDb.id,
                    documentType = documentRequest.documentType,
                    title = documentRequest.title,
                    description = documentRequest.description
                )
            } else {
                documentDb
            }
        }
        return updatedDocuments + newDocuments.toBidEntityBusinessFunctionDocuments()
    }

    private fun updateAdditionalIdentifiers(
        additionalIdentifiersDb: List<Identifier>?,
        additionalIdentifiersRequest: List<BidUpdateData.Bid.Tenderer.AdditionalIdentifier>
    ): List<Identifier> {
        val additionalIdentifiersEntities = additionalIdentifiersDb ?: emptyList<Identifier>()

        val newAdditionalIdentifiers =
            additionalIdentifiersRequest.filter { it.id !in additionalIdentifiersEntities.map { it.id } }
        val updatedAdditionalIdentifiers = additionalIdentifiersEntities.map { additionalIdentifierDb ->
            if (additionalIdentifierDb.id in additionalIdentifiersRequest.map { it.id }) {
                val additionalIdentifierRequest =
                    additionalIdentifiersRequest.find { it.id == additionalIdentifierDb.id }!!
                Identifier(
                    id = additionalIdentifierDb.id,
                    scheme = additionalIdentifierDb.scheme,
                    legalName = additionalIdentifierRequest.legalName,
                    uri = additionalIdentifierRequest.uri
                )
            } else {
                additionalIdentifierDb
            }
        }
        return updatedAdditionalIdentifiers + newAdditionalIdentifiers.toBidEntityAdditionalIdentifies()
    }

    private fun updateDetails(detailsDb: Details, detailsRequest: BidUpdateData.Bid.Tenderer.Details?): Details {
        if (detailsRequest == null) return detailsDb

        return Details(
            typeOfSupplier = detailsDb.typeOfSupplier,
            mainEconomicActivities = detailsRequest.mainEconomicActivities
                .map { mainEconomicActivity ->
                    MainEconomicActivity(
                        id = mainEconomicActivity.id,
                        description = mainEconomicActivity.description,
                        uri = mainEconomicActivity.uri,
                        scheme = mainEconomicActivity.scheme
                    )
                },
            scale = detailsDb.scale,
            permits = updatePermits(detailsDb.permits, detailsRequest.permits),
            bankAccounts = updateBankAccounts(detailsDb.bankAccounts, detailsRequest.bankAccounts),
            legalForm = updateLegalForm(detailsDb.legalForm, detailsRequest.legalForm)
        )
    }

    private fun updatePermits(
        permitsDb: List<Permit>?,
        permitsRequest: List<BidUpdateData.Bid.Tenderer.Details.Permit>
    ): List<Permit>? {
        if (permitsRequest.isEmpty()) return permitsDb

        return permitsRequest.map { permitRequest ->
            Permit(
                id = permitRequest.id,
                scheme = permitRequest.scheme,
                url = permitRequest.url,
                permitDetails = PermitDetails(
                    issuedBy = IssuedBy(
                        id = permitRequest.permitDetails.issuedBy.id,
                        name = permitRequest.permitDetails.issuedBy.name
                    ),
                    issuedThought = IssuedThought(
                        id = permitRequest.permitDetails.issuedThought.id,
                        name = permitRequest.permitDetails.issuedThought.name
                    ),
                    validityPeriod = ValidityPeriod(
                        startDate = permitRequest.permitDetails.validityPeriod.startDate,
                        endDate = permitRequest.permitDetails.validityPeriod.endDate
                    )
                )
            )
        }
    }

    private fun updateBankAccounts(
        bankAccountsDb: List<BankAccount>?,
        bankAccountsRequest: List<BidUpdateData.Bid.Tenderer.Details.BankAccount>
    ): List<BankAccount>? {
        if (bankAccountsRequest.isEmpty()) return bankAccountsDb

        return bankAccountsRequest.map { bankAccount ->
            BankAccount(
                description = bankAccount.description,
                bankName = bankAccount.bankName,
                address = Address(
                    streetAddress = bankAccount.address.streetAddress,
                    postalCode = bankAccount.address.postalCode,
                    addressDetails = AddressDetails(
                        country = CountryDetails(
                            id = bankAccount.address.addressDetails.country.id,
                            scheme = bankAccount.address.addressDetails.country.scheme,
                            description = bankAccount.address.addressDetails.country.description,
                            uri = bankAccount.address.addressDetails.country.uri
                        ),
                        region = RegionDetails(
                            id = bankAccount.address.addressDetails.region.id,
                            scheme = bankAccount.address.addressDetails.region.scheme,
                            description = bankAccount.address.addressDetails.region.description,
                            uri = bankAccount.address.addressDetails.region.uri
                        ),
                        locality = LocalityDetails(
                            id = bankAccount.address.addressDetails.locality.id,
                            scheme = bankAccount.address.addressDetails.locality.scheme,
                            description = bankAccount.address.addressDetails.locality.description,
                            uri = bankAccount.address.addressDetails.locality.uri
                        )
                    )
                ),
                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.map { additionalAccountIdentifier ->
                    AdditionalAccountIdentifier(
                        id = additionalAccountIdentifier.id,
                        scheme = additionalAccountIdentifier.scheme
                    )
                },
                identifier = BankAccount.Identifier(
                    id = bankAccount.identifier.id,
                    scheme = bankAccount.identifier.scheme
                ),
                accountIdentification = AccountIdentification(
                    id = bankAccount.accountIdentification.id,
                    scheme = bankAccount.accountIdentification.scheme
                )
            )
        }
    }

    private fun updateLegalForm(
        legalFormDb: LegalForm?,
        legalFormRequest: BidUpdateData.Bid.Tenderer.Details.LegalForm?
    ): LegalForm? {
        if (legalFormRequest == null) return legalFormDb

        return LegalForm(
            id = legalFormRequest.id,
            scheme = legalFormRequest.scheme,
            description = legalFormRequest.description,
            uri = legalFormRequest.uri
        )
    }

    private fun checkBusinessFunctionsPeriod(bid: BidUpdateData.Bid, requestDate: LocalDateTime) {
        fun BidUpdateData.Bid.Tenderer.Persone.BusinessFunction.Period.validate() {
            if (this.startDate > requestDate) throw ErrorException(
                error = INVALID_DATE,
                message = "Period.startDate specified in  business functions cannot be greater than startDate from request."
            )
        }

        bid.tenderers.flatMap { it.persones }
            .flatMap { it.businessFunctions }
            .map { it.period }
            .forEach { it.validate() }
    }

    private fun checkRelatedLots(bidEntity: Bid, bidRequest: BidUpdateData.Bid) {
        if (!bidEntity.relatedLots.containsAll(bidRequest.relatedLots))
            throw ErrorException(
                error = INVALID_RELATED_LOT,
                message = "Some of related lots from request is missing in database. " +
                    "Saved related lots: ${bidEntity.relatedLots}. " +
                    "Related lots from request: ${bidRequest.relatedLots}. "
            )
    }

    private fun checkTenderers(cpid: Cpid, ocid: Ocid, bidDto: BidCreateData.Bid) {
        val bidEntities = bidRepository.findBy(cpid, ocid)
            .orThrow { it.exception }
        if (bidEntities.isNotEmpty()) {
            val receivedRelatedLots = bidDto.relatedLots.toSet()
            val idsReceivedTenderers = bidDto.tenderers.toSetBy { it.id }
            bidEntities.asSequence()
                .filter { entity -> entity.status != Status.WITHDRAWN }
                .map { entity -> toObject(Bid::class.java, entity.jsonData) }
                .forEach { bid ->
                    val idsTenderers: Set<String> = bid.tenderers.toSetBy { it.id!! }
                    val relatedLots: Set<String> = bid.relatedLots.toSet()
                    if (idsReceivedTenderers.any { it in idsTenderers } && receivedRelatedLots.any { it in relatedLots })
                        throw ErrorException(BID_ALREADY_WITH_LOT)
                }
        }
    }

    private fun List<BidCreateData.Bid.Document>.toBidEntityDocuments(): List<Document> {
        return this.map { document ->
            Document(
                id = document.id,
                description = document.description,
                title = document.title,
                documentType = document.documentType,
                relatedLots = document.relatedLots
            )
        }
    }

    private fun List<BidCreateData.Bid.Tenderer>.toBidEntityTenderers(): List<Organization> {
        return this.map { tenderer ->
            Organization(
                id = tenderer.id,
                name = tenderer.name,
                identifier = Identifier(
                    id = tenderer.identifier.id,
                    scheme = tenderer.identifier.scheme,
                    legalName = tenderer.identifier.legalName,
                    uri = tenderer.identifier.uri
                ),
                additionalIdentifiers = tenderer.additionalIdentifiers.map { additionalIdentifier ->
                    Identifier(
                        id = additionalIdentifier.id,
                        scheme = additionalIdentifier.scheme,
                        legalName = additionalIdentifier.legalName,
                        uri = additionalIdentifier.uri
                    )
                },
                address = Address(
                    streetAddress = tenderer.address.streetAddress,
                    postalCode = tenderer.address.postalCode,
                    addressDetails = AddressDetails(
                        country = CountryDetails(
                            id = tenderer.address.addressDetails.country.id,
                            scheme = tenderer.address.addressDetails.country.scheme,
                            description = tenderer.address.addressDetails.country.description,
                            uri = tenderer.address.addressDetails.country.uri
                        ),
                        region = RegionDetails(
                            id = tenderer.address.addressDetails.region.id,
                            scheme = tenderer.address.addressDetails.region.scheme,
                            description = tenderer.address.addressDetails.region.description,
                            uri = tenderer.address.addressDetails.region.uri
                        ),
                        locality = LocalityDetails(
                            id = tenderer.address.addressDetails.locality.id,
                            scheme = tenderer.address.addressDetails.locality.scheme,
                            description = tenderer.address.addressDetails.locality.description,
                            uri = tenderer.address.addressDetails.locality.uri
                        )
                    )
                ),
                contactPoint = ContactPoint(
                    name = tenderer.contactPoint.name,
                    email = tenderer.contactPoint.email,
                    telephone = tenderer.contactPoint.telephone,
                    faxNumber = tenderer.contactPoint.faxNumber,
                    url = tenderer.contactPoint.url
                ),
                details = Details(
                    typeOfSupplier = tenderer.details.typeOfSupplier,
                    mainEconomicActivities = tenderer.details.mainEconomicActivities
                        .map { mainEconomicActivity ->
                            MainEconomicActivity(
                                id = mainEconomicActivity.id,
                                description = mainEconomicActivity.description,
                                uri = mainEconomicActivity.uri,
                                scheme = mainEconomicActivity.scheme
                            )
                        },
                    permits = tenderer.details.permits.map { permit ->
                        Permit(
                            id = permit.id,
                            scheme = permit.scheme,
                            url = permit.url,
                            permitDetails = PermitDetails(
                                issuedBy = IssuedBy(
                                    id = permit.permitDetails.issuedBy.id,
                                    name = permit.permitDetails.issuedBy.name
                                ),
                                issuedThought = IssuedThought(
                                    id = permit.permitDetails.issuedThought.id,
                                    name = permit.permitDetails.issuedThought.name
                                ),
                                validityPeriod = ValidityPeriod(
                                    startDate = permit.permitDetails.validityPeriod.startDate,
                                    endDate = permit.permitDetails.validityPeriod.endDate
                                )
                            )
                        )
                    },
                    scale = tenderer.details.scale,
                    bankAccounts = tenderer.details.bankAccounts.map { bankAccount ->
                        BankAccount(
                            description = bankAccount.description,
                            bankName = bankAccount.bankName,
                            address = Address(
                                streetAddress = bankAccount.address.streetAddress,
                                postalCode = bankAccount.address.postalCode,
                                addressDetails = AddressDetails(
                                    country = CountryDetails(
                                        id = bankAccount.address.addressDetails.country.id,
                                        scheme = bankAccount.address.addressDetails.country.scheme,
                                        description = bankAccount.address.addressDetails.country.description,
                                        uri = bankAccount.address.addressDetails.country.uri
                                    ),
                                    region = RegionDetails(
                                        id = bankAccount.address.addressDetails.region.id,
                                        scheme = bankAccount.address.addressDetails.region.scheme,
                                        description = bankAccount.address.addressDetails.region.description,
                                        uri = bankAccount.address.addressDetails.region.uri
                                    ),
                                    locality = LocalityDetails(
                                        id = bankAccount.address.addressDetails.locality.id,
                                        scheme = bankAccount.address.addressDetails.locality.scheme,
                                        description = bankAccount.address.addressDetails.locality.description,
                                        uri = bankAccount.address.addressDetails.locality.uri
                                    )
                                )
                            ),
                            identifier = BankAccount.Identifier(
                                id = bankAccount.identifier.id,
                                scheme = bankAccount.identifier.scheme
                            ),
                            accountIdentification = AccountIdentification(
                                id = bankAccount.accountIdentification.id,
                                scheme = bankAccount.accountIdentification.scheme
                            ),
                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers.map { accountIdentifier ->
                                AdditionalAccountIdentifier(
                                    id = accountIdentifier.id,
                                    scheme = accountIdentifier.scheme
                                )
                            }
                        )
                    },
                    legalForm = tenderer.details.legalForm?.let { legalForm ->
                        LegalForm(
                            id = legalForm.id,
                            scheme = legalForm.scheme,
                            description = legalForm.description,
                            uri = legalForm.uri
                        )
                    }
                ),
                persones = tenderer.persones.map { person ->
                    Persone(
                        id = PersonId.generate(
                            scheme = person.identifier.scheme,
                            id = person.identifier.id
                        ),
                        title = person.title,
                        name = person.name,
                        identifier = Persone.Identifier(
                            id = person.identifier.id,
                            scheme = person.identifier.scheme,
                            uri = person.identifier.uri
                        ),
                        businessFunctions = person.businessFunctions.map { businessFunction ->
                            BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = BusinessFunction.Period(
                                    startDate = businessFunction.period.startDate
                                ),
                                documents = businessFunction.documents.map { document ->
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
            )
        }
    }

    private fun List<BidCreateData.Bid.RequirementResponse>.toBidEntityRequirementResponse(): List<RequirementResponse> {
        return this.map { requirementResponse ->
            RequirementResponse(
                id = requirementResponse.id,
                title = requirementResponse.title,
                description = requirementResponse.description,
                value = requirementResponse.value,
                requirement = Requirement(
                    id = requirementResponse.requirement.id
                ),
                period = requirementResponse.period?.let { period ->
                    Period(
                        startDate = period.startDate,
                        endDate = period.endDate
                    )
                }
            )
        }
    }

    private fun List<BidUpdateData.Bid.Tenderer.Persone>.toBidEntityPersones(): List<Persone> {
        return this.map { persone ->
            Persone(
                id = PersonId.generate(
                    scheme = persone.identifier.scheme,
                    id = persone.identifier.id
                ),
                title = persone.title,
                name = persone.name,
                identifier = Persone.Identifier(
                    id = persone.identifier.id,
                    scheme = persone.identifier.scheme,
                    uri = persone.identifier.uri
                ),
                businessFunctions = persone.businessFunctions.map { businessfunction ->
                    BusinessFunction(
                        id = businessfunction.id,
                        type = businessfunction.type,
                        jobTitle = businessfunction.jobTitle,
                        period = BusinessFunction.Period(
                            startDate = businessfunction.period.startDate
                        ),
                        documents = businessfunction.documents.map { document ->
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
    }

    private fun List<BidUpdateData.Bid.Tenderer.Persone.BusinessFunction>.toBidEntityBusinessFunction(): List<BusinessFunction> {
        return this.map { businessFunction ->
            BusinessFunction(
                id = businessFunction.id,
                type = businessFunction.type,
                jobTitle = businessFunction.jobTitle,
                period = BusinessFunction.Period(
                    startDate = businessFunction.period.startDate
                ),
                documents = businessFunction.documents.map { document ->
                    BusinessFunction.Document(
                        id = document.id,
                        documentType = document.documentType,
                        title = document.title,
                        description = document.description
                    )
                }
            )
        }
    }

    private fun List<BidUpdateData.Bid.Tenderer.Persone.BusinessFunction.Document>.toBidEntityBusinessFunctionDocuments(): List<BusinessFunction.Document> {
        return this.map { document ->
            BusinessFunction.Document(
                id = document.id,
                documentType = document.documentType,
                title = document.title,
                description = document.description
            )
        }
    }

    private fun List<BidUpdateData.Bid.Tenderer.AdditionalIdentifier>.toBidEntityAdditionalIdentifies(): List<Identifier> {
        return this.map { additionalIdentifier ->
            Identifier(
                id = additionalIdentifier.id,
                scheme = additionalIdentifier.scheme,
                legalName = additionalIdentifier.legalName,
                uri = additionalIdentifier.uri
            )
        }
    }

    fun openBidDocs(context: OpenBidDocsContext, data: OpenBidDocsData): OpenBidDocsResult {
        val bidEntity = bidRepository.findBy(context.cpid, context.ocid, data.bidId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bid = toObject(Bid::class.java, bidEntity.jsonData)

        return OpenBidDocsResult(
            bid = OpenBidDocsResult.Bid(
                id = BidId.fromString(bid.id),
                documents = bid.documents
                    ?.map { document ->
                        OpenBidDocsResult.Bid.Document(
                            description = document.description,
                            id = document.id,
                            relatedLots = document.relatedLots
                                ?.map { lotId -> LotId.fromString(lotId) }
                                .orEmpty(),
                            documentType = document.documentType,
                            title = document.title
                        )
                    }
                    .orEmpty()
            )
        )
    }

    fun getBidsByLots(context: GetBidsByLotsContext, data: GetBidsByLotsData): GetBidsByLotsResult {
        val lotsIds = data.lots
            .toSetBy { it.id.toString() }
        val bids = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
            .asSequence()
            .filter { entity -> entity.status == Status.PENDING }
            .map { bidEntity -> toObject(Bid::class.java, bidEntity.jsonData) }
            .filter { bid ->
                bid.status == Status.PENDING
                    && bid.statusDetails == StatusDetails.EMPTY
                    && lotsIds.containsAny(bid.relatedLots)
            }
            .toList()
        return GetBidsByLotsResult(
            bids = bids.map { bid ->
                GetBidsByLotsResult.Bid(
                    id = BidId.fromString(bid.id),
                    documents = bid.documents
                        ?.map { document ->
                            GetBidsByLotsResult.Bid.Document(
                                id = document.id,
                                relatedLots = document.relatedLots
                                    ?.map { relatedLot -> LotId.fromString(relatedLot) }
                                    .orEmpty(),
                                description = document.description,
                                title = document.title,
                                documentType = document.documentType
                            )
                        }
                        .orEmpty(),
                    relatedLots = bid.relatedLots
                        .map { relatedLot -> UUID.fromString(relatedLot) },
                    statusDetails = bid.statusDetails,
                    status = bid.status,
                    tenderers = bid.tenderers
                        .map { tender ->
                            GetBidsByLotsResult.Bid.Tenderer(
                                id = tender.id,
                                name = tender.name,
                                identifier = tender.identifier
                                    .let { identifier ->
                                        GetBidsByLotsResult.Bid.Tenderer.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                address = tender.address
                                    .let { address ->
                                        GetBidsByLotsResult.Bid.Tenderer.Address(
                                            postalCode = address.postalCode,
                                            streetAddress = address.streetAddress,
                                            addressDetails = address.addressDetails
                                                .let { addressDetail ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails(
                                                        country = addressDetail.country
                                                            .let { country ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Country(
                                                                    id = country.id,
                                                                    scheme = country.scheme,
                                                                    description = country.description,
                                                                    uri = country.uri
                                                                )
                                                            },
                                                        locality = addressDetail.locality
                                                            .let { locality ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Locality(
                                                                    id = locality.id,
                                                                    scheme = locality.scheme,
                                                                    description = locality.description,
                                                                    uri = locality.uri
                                                                )
                                                            },
                                                        region = addressDetail.region
                                                            .let { region ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Address.AddressDetails.Region(
                                                                    id = region.id,
                                                                    scheme = region.scheme,
                                                                    description = region.description,
                                                                    uri = region.uri
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    },
                                details = tender.details
                                    .let { detail ->
                                        GetBidsByLotsResult.Bid.Tenderer.Details(
                                            typeOfSupplier = detail.typeOfSupplier
                                                ?.let { TypeOfSupplier.creator(it) },
                                            mainEconomicActivities = detail.mainEconomicActivities
                                                ?.map { mainEconomicActivity ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.MainEconomicActivity(
                                                        id = mainEconomicActivity.id,
                                                        description = mainEconomicActivity.description,
                                                        uri = mainEconomicActivity.uri,
                                                        scheme = mainEconomicActivity.scheme
                                                    )
                                                }
                                                .orEmpty(),
                                            scale = Scale.creator(detail.scale),
                                            permits = detail.permits
                                                ?.map { permit ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.Permit(
                                                        id = permit.id,
                                                        scheme = permit.scheme,
                                                        url = permit.url,
                                                        permitDetails = permit.permitDetails
                                                            .let { permitDetail ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails(
                                                                    issuedBy = permitDetail.issuedBy
                                                                        .let { issuedBy ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                                id = issuedBy.id,
                                                                                name = issuedBy.name
                                                                            )
                                                                        },
                                                                    issuedThought = permitDetail.issuedThought
                                                                        .let { issuedThought ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                                id = issuedThought.id,
                                                                                name = issuedThought.name
                                                                            )
                                                                        },
                                                                    validityPeriod = permitDetail.validityPeriod
                                                                        .let { validityPeriod ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                                startDate = validityPeriod.startDate,
                                                                                endDate = validityPeriod.endDate
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                }
                                                .orEmpty(),
                                            legalForm = detail.legalForm
                                                ?.let { legalForm ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.LegalForm(
                                                        scheme = legalForm.scheme,
                                                        id = legalForm.id,
                                                        description = legalForm.description,
                                                        uri = legalForm.uri
                                                    )
                                                },
                                            bankAccounts = detail.bankAccounts
                                                ?.map { bankAccount ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount(
                                                        description = bankAccount.description,
                                                        bankName = bankAccount.bankName,
                                                        identifier = bankAccount.identifier
                                                            .let { identifier ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Identifier(
                                                                    id = identifier.id,
                                                                    scheme = identifier.scheme
                                                                )
                                                            },
                                                        address = bankAccount.address
                                                            .let { address ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address(
                                                                    streetAddress = address.streetAddress,
                                                                    postalCode = address.postalCode,
                                                                    addressDetails = address.addressDetails
                                                                        .let { addressDetail ->
                                                                            GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                                country = addressDetail.country
                                                                                    .let { country ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                                            id = country.id,
                                                                                            scheme = country.scheme,
                                                                                            description = country.description,
                                                                                            uri = country.uri
                                                                                        )
                                                                                    },
                                                                                locality = addressDetail.locality
                                                                                    .let { locality ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                                            id = locality.id,
                                                                                            scheme = locality.scheme,
                                                                                            description = locality.description,
                                                                                            uri = locality.uri
                                                                                        )
                                                                                    },
                                                                                region = addressDetail.region
                                                                                    .let { region ->
                                                                                        GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                                            id = region.id,
                                                                                            scheme = region.scheme,
                                                                                            description = region.description,
                                                                                            uri = region.uri
                                                                                        )
                                                                                    }
                                                                            )
                                                                        }
                                                                )
                                                            },
                                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                            ?.map { additionalAccountIdentifier ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                                    id = additionalAccountIdentifier.id,
                                                                    scheme = additionalAccountIdentifier.scheme
                                                                )
                                                            }
                                                            .orEmpty(),
                                                        accountIdentification = bankAccount.accountIdentification
                                                            .let { accountIdentification ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                                    scheme = accountIdentification.scheme,
                                                                    id = accountIdentification.id
                                                                )
                                                            }
                                                    )
                                                }
                                                .orEmpty()
                                        )
                                    },
                                contactPoint = tender.contactPoint
                                    .let { contactPoint ->
                                        GetBidsByLotsResult.Bid.Tenderer.ContactPoint(
                                            name = contactPoint.name,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            email = contactPoint.email!!,
                                            url = contactPoint.url
                                        )
                                    },
                                additionalIdentifiers = tender.additionalIdentifiers
                                    ?.map { additionalIdentifier ->
                                        GetBidsByLotsResult.Bid.Tenderer.AdditionalIdentifier(
                                            id = additionalIdentifier.id,
                                            scheme = additionalIdentifier.scheme,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    }
                                    .orEmpty(),
                                persones = tender.persones
                                    ?.map { person ->
                                        GetBidsByLotsResult.Bid.Tenderer.Persone(
                                            identifier = person.identifier
                                                .let { identifier ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Persone.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme,
                                                        uri = identifier.uri
                                                    )
                                                },
                                            name = person.name,
                                            title = person.title,
                                            businessFunctions = person.businessFunctions
                                                .map { businessFunction ->
                                                    GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction(
                                                        id = businessFunction.id,
                                                        type = businessFunction.type,
                                                        jobTitle = businessFunction.jobTitle,
                                                        documents = businessFunction.documents
                                                            ?.map { document ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction.Document(
                                                                    id = document.id,
                                                                    documentType = document.documentType,
                                                                    title = document.title,
                                                                    description = document.description
                                                                )
                                                            }
                                                            .orEmpty(),
                                                        period = businessFunction.period
                                                            .let { period ->
                                                                GetBidsByLotsResult.Bid.Tenderer.Persone.BusinessFunction.Period(
                                                                    startDate = period.startDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    }
                                    .orEmpty()
                            )
                        },
                    requirementResponses = bid.requirementResponses
                        ?.map { requirementResponse ->
                            GetBidsByLotsResult.Bid.RequirementResponse(
                                id = requirementResponse.id,
                                description = requirementResponse.description,
                                title = requirementResponse.title,
                                value = requirementResponse.value,
                                period = requirementResponse.period
                                    ?.let { period ->
                                        GetBidsByLotsResult.Bid.RequirementResponse.Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                requirement = GetBidsByLotsResult.Bid.RequirementResponse.Requirement(
                                    id = requirementResponse.requirement.id
                                )
                            )
                        }
                        .orEmpty(),
                    date = bid.date,
                    value = bid.value!!
                )
            }
        )
    }

    fun validateBidData(params: ValidateBidDataParams): Validated<Fail> {
        checkBidsValue(params).onFailure { return it.reason.asValidationError() }
        checkTenderers(params).onFailure { return it.reason.asValidationError() }
        checkDocuments(params).onFailure { return it.reason.asValidationError() }
        checkItems(params).onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun checkBidsValue(params: ValidateBidDataParams): Validated<Fail.Error> {
        val requiresElectronicCatalogue = params.tender.procurementMethodModalities
            .any { it == ProcurementMethodModalities.REQUIRES_ELECTRONIC_CATALOGUE }

        if (!requiresElectronicCatalogue) {
            val bid = params.bids.details.first()
            val value = bid.value ?: return ValidationError.MissingBidValue(bid.id).asValidationError()

            if (value.amount.value <= BigDecimal.ZERO)
                return ValidationError.InvalidBidAmount(bid.id).asValidationError()

            if (value.currency != params.tender.value.currency)
                return ValidationError.InvalidBidCurrency(bid.id).asValidationError()
        }

        return Validated.ok()
    }

    private fun checkTenderers(params: ValidateBidDataParams): Validated<Fail> {
        val tenderers = params.bids.details.first().tenderers
        val duplicateTenderer = tenderers.getDuplicate { it.id }
        if (duplicateTenderer != null)
            return ValidationError.DuplicateTenderers(duplicateTenderer.id).asValidationError()

        checkForActiveInvitations(params)
            .onFailure { return it.reason.asValidationError() }

        checkForDuplicatePersonBusinessFunctions(tenderers)
            .onFailure { return it.reason.asValidationError() }

        checkForDuplicatePersonDocuments(tenderers)
            .onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun checkForActiveInvitations(params: ValidateBidDataParams): Validated<Fail> {
        val activeInvitations = invitationRepository.findBy(params.cpid)
            .onFailure { return it.reason.asValidationError() }
            .filter { invitation -> invitation.status == InvitationStatus.ACTIVE }

        val groupsOfTenderers = activeInvitations
            .asSequence()
            .map { invitation -> invitation.tenderers.toSetBy { it.id } }
            .toSet()

        val receivedTenderers = params.bids.details.first().tenderers.toSetBy { it.id }

        if (receivedTenderers !in groupsOfTenderers)
            return ValidationError.ActiveInvitationNotFound().asValidationError()

        return Validated.ok()
    }

    private fun checkForDuplicatePersonDocuments(tenderers: List<ValidateBidDataParams.Bids.Detail.Tenderer>): Validated<Fail.Error> {
        tenderers.flatMap { tenderer -> tenderer.persones }
            .map { person ->
                val duplicateDocuments = person.businessFunctions
                    .flatMap { businessFunction -> businessFunction.documents }
                    .getDuplicate { it.id }

                if (duplicateDocuments != null)
                    return ValidationError.DuplicatePersonDocuments(person.id, duplicateDocuments.id)
                        .asValidationError()
            }
        return Validated.ok()
    }

    private fun checkForDuplicatePersonBusinessFunctions(tenderers: List<ValidateBidDataParams.Bids.Detail.Tenderer>): Validated<Fail.Error> {
        tenderers.flatMap { tenderer -> tenderer.persones }
            .map { person ->
                val duplicateBusinessFunction = person.businessFunctions.getDuplicate { it.id }
                if (duplicateBusinessFunction != null)
                    return ValidationError.DuplicatePersonBusinessFunctions(person.id, duplicateBusinessFunction.id)
                        .asValidationError()
            }

        return Validated.ok()
    }

    private fun checkDocuments(params: ValidateBidDataParams): Validated<Fail.Error> {
        val bid = params.bids.details.first()
        checkForDuplicateBidDocument(bid).onFailure { return it.reason.asValidationError() }
        checkRelatedLots(bid).onFailure { return it.reason.asValidationError() }

        return Validated.ok()
    }

    private fun checkForDuplicateBidDocument(bid: ValidateBidDataParams.Bids.Detail): Validated<Fail.Error> {
        val documents = bid.documents

        if (documents.isNotEmpty()) {
            val duplicateDocument = documents.getDuplicate { it.id }
            if (duplicateDocument != null)
                return ValidationError.DuplicateDocuments(bid.id, duplicateDocument.id).asValidationError()
        }
        return Validated.ok()
    }

    private fun checkRelatedLots(bid: ValidateBidDataParams.Bids.Detail): Validated<Fail.Error> {
        val documentsRelatedLots = bid.documents.flatMap { it.relatedLots }.toSet()
        if (documentsRelatedLots.isNotEmpty()) {
            if (documentsRelatedLots != bid.relatedLots.toSet())
                return ValidationError.InvalidRelatedLots().asValidationError()
        }
        return Validated.ok()
    }

    private fun checkItems(params: ValidateBidDataParams): Validated<Fail> {
        val requiresElectronicCatalogue = params.tender.procurementMethodModalities
            .any { it == ProcurementMethodModalities.REQUIRES_ELECTRONIC_CATALOGUE }

        if (requiresElectronicCatalogue) {
            val bid = params.bids.details.first()

            if (bid.items.isEmpty())
                return ValidationError.MissingItems().asValidationError()

            val duplicateItem = bid.items.getDuplicate { it.id }
            if (duplicateItem != null)
                return ValidationError.DuplicateItems(bid.id, duplicateItem.id).asValidationError()

            checkTenderItemsContainBidItems(bid, params).onFailure { return it.reason.asValidationError() }
            checkItemValue(bid, params).onFailure { return it.reason.asValidationError() }
            checkBidAndTenderUnitEquality(bid, params).onFailure { return it.reason.asValidationError() }
        }

        return Validated.ok()
    }

    private fun checkItemValue(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        bid.items.map { item ->
            val value = item.unit.value
            if (value.amount.value <= BigDecimal.ZERO)
                return ValidationError.InvalidItemAmount(item.id).asValidationError()

            if (value.currency != params.tender.value.currency)
                return ValidationError.InvalidItemCurrency(item.id).asValidationError()
        }

        return Validated.ok()
    }

    private fun checkTenderItemsContainBidItems(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        val bidItems = bid.items.toSetBy { it.id }
        val tenderItems = params.tender.items.toSetBy { it.id }
        if(!tenderItems.containsAll(bidItems))
            return ValidationError.InvalidItems().asValidationError()

        return Validated.ok()
    }

    private fun checkBidAndTenderUnitEquality(
        bid: ValidateBidDataParams.Bids.Detail,
        params: ValidateBidDataParams
    ): Validated<Fail> {
        val bidUnits = bid.items.toSetBy { it.unit.id }
        val tenderUnits = params.tender.items.toSetBy { it.unit.id }
        if (bidUnits != tenderUnits)
            return ValidationError.InvalidUnits().asValidationError()

        return Validated.ok()
    }

    fun createBid(params: CreateBidParams): Result<CreateBidResult, Fail> {
        val bidEntities = bidRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it }
            .asSequence()
            .map { entity -> entity.bidId to entity }
            .toMap()

        val bids = bidEntities.values
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val receivedBid = params.bids.details.first()
        val receivedTenderers = receivedBid
            .tenderers
            .toSetBy { it.id }


        val bidToWithdraw = bids.firstOrNull { bid ->
            containsActiveBidByReceivedTenderersAndLot(bid, receivedTenderers, receivedBid)
        }

        val updatedBid = bidToWithdraw?.withdrawBid()
        val updatedBidEntity = updatedBid
            ?.let { bid ->
                val entity = bidEntities.getValue(BidId.fromString(bid.id))
                BidEntity.Updated(
                    cpid = entity.cpid,
                    ocid = entity.ocid,
                    createdDate = entity.createdDate,
                    pendingDate = entity.pendingDate,
                    bid = bid
                )
            }

        val createdBid = receivedBid
            .convert(params.date)
        val createdBidEntity = BidEntity.New(
            cpid = params.cpid,
            ocid = params.ocid,
            token = generationService.generateToken(),
            owner = params.owner,
            createdDate = params.date,
            pendingDate = params.date,
            bid = createdBid
        )

        bidRepository.save(listOfNotNull(updatedBidEntity, createdBidEntity))

        return createdBid.convertToCreateBidResult(createdBidEntity.token).asSuccess()
    }

    private fun containsActiveBidByReceivedTenderersAndLot(
        storedBid: Bid,
        receivedTenderers: Set<String>,
        receivedBid: CreateBidParams.Bids.Detail
    ): Boolean {
        val storedBidTenderers = storedBid.tenderers.toSetBy { it.id }
        val storedLot = storedBid.relatedLots.first()
        val receivedLot = receivedBid.relatedLots.first()
        return storedBid.isActive()
            && storedBidTenderers == receivedTenderers
            && storedLot == receivedLot
    }

    fun Bid.isActive(): Boolean = status == Status.PENDING && statusDetails == StatusDetails.EMPTY

    fun Bid.withdrawBid() = copy(status = Status.WITHDRAWN)
}

fun checkTenderersInvitations(
    cpid: Cpid,
    pmd: ProcurementMethod,
    tenderers: List<BidCreateData.Bid.Tenderer>,
    getInvitations: (Cpid) -> Set<String>
) {
    when (pmd) {
        ProcurementMethod.CD, ProcurementMethod.TEST_CD,
        ProcurementMethod.CF, ProcurementMethod.TEST_CF,
        ProcurementMethod.DA, ProcurementMethod.TEST_DA,
        ProcurementMethod.DC, ProcurementMethod.TEST_DC,
        ProcurementMethod.FA, ProcurementMethod.TEST_FA,
        ProcurementMethod.IP, ProcurementMethod.TEST_IP,
        ProcurementMethod.MV, ProcurementMethod.TEST_MV,
        ProcurementMethod.NP, ProcurementMethod.TEST_NP,
        ProcurementMethod.OF, ProcurementMethod.TEST_OF,
        ProcurementMethod.OP, ProcurementMethod.TEST_OP,
        ProcurementMethod.OT, ProcurementMethod.TEST_OT,
        ProcurementMethod.SV, ProcurementMethod.TEST_SV -> Unit

        ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
        ProcurementMethod.RT, ProcurementMethod.TEST_RT -> {
            val tenderersIds = tenderers.map { it.id }
            val activeInvitations = getInvitations(cpid)
            checkTenderersInvitedToTender(tenderersIds, activeInvitations)
        }
    }
}

fun checkTenderersInvitedToTender(bidTenderers: List<String>, activeInvitations: Set<String>) {
    bidTenderers.forEach { id ->
        activeInvitations.find { it == id }
            ?: throw ErrorException(
                error = ErrorType.RELATION_NOT_FOUND,
                message = "Cannot found active invitation for tenderer with id='${id}'"
            )
    }
}
