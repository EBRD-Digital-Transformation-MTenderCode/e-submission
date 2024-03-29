package com.procurement.submission.infrastructure.service.bid

import com.procurement.submission.application.params.GetSuppliersOwnersParams
import com.procurement.submission.application.params.bid.query.find.FindDocumentsByBidIdsErrors
import com.procurement.submission.application.params.bid.query.find.FindDocumentsByBidIdsParams
import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsErrors
import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsParams
import com.procurement.submission.application.params.bid.query.get.GetOrganizationsByReferencesFromPacsErrors
import com.procurement.submission.application.params.bid.query.get.GetOrganizationsByReferencesFromPacsParams
import com.procurement.submission.application.params.errors.GetSuppliersOwnersErrors
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.service.BidQueryService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.extension.toSetBy
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.PartyRole
import com.procurement.submission.infrastructure.handler.v2.converter.fromDomain
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetOrganizationsByReferencesFromPacsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.GetSuppliersOwnersResponse
import com.procurement.submission.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import org.springframework.stereotype.Service

@Service
class BidQueryServiceImpl(
    private val bidRepository: BidRepository,
    private val transform: Transform
) : BidQueryService {

    override fun getBidsForPacs(params: GetBidsForPacsParams): Result<GetBidsForPacsResult, Fail> {
        val receivedRelatedLots = params.tender.lots.map { it.id }
        fun Bid.isRelatedLotMatched(): Boolean = this.relatedLots.any { it in receivedRelatedLots }

        val targetBids = bidRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }
            .filter { it.isRelatedLotMatched() }

        val targetBidsRelatedLots = targetBids.map { it.relatedLots }.flatten()

        if (!targetBidsRelatedLots.containsAll(receivedRelatedLots))
            return GetBidsForPacsErrors.BidsNotFound(receivedRelatedLots - targetBidsRelatedLots).asFailure()
        else
            return targetBids
                .map { GetBidsForPacsResult.ResponseConverter.fromDomain(it) }
                .let { GetBidsForPacsResult(bids = GetBidsForPacsResult.Bids(details = it)) }
                .asSuccess()
    }

    override fun getOrganizationsByReferencesFromPacs(params: GetOrganizationsByReferencesFromPacsParams): Result<GetOrganizationsByReferencesFromPacsResult, Fail> {
        val targetBids = bidRepository
            .findBy(params.cpid, params.ocid).onFailure { return it }
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        if (targetBids.isEmpty())
            return GetOrganizationsByReferencesFromPacsErrors.BidsNotFound(params.cpid, params.ocid).asFailure()

        val receivedOrganizationsIds = params.parties.toSetBy { it.id }
        val allOrganizationsById = targetBids
            .flatMap { it.tenderers }
            .associateBy { it.id!! }

        // FR.COM-13.16.1
        val targetOrganizations = receivedOrganizationsIds
            .map { id ->
                allOrganizationsById[id] ?: return GetOrganizationsByReferencesFromPacsErrors.OrganizationNotFound(id)
                    .asFailure()
            }

        // FR.COM-13.16.3
        return targetOrganizations
            .map {
                GetOrganizationsByReferencesFromPacsResult.Party.fromDomain(
                    it,
                    PartyRole.SUPPLIER
                )
            } // FR.COM-13.16.2
            .let { GetOrganizationsByReferencesFromPacsResult(parties = it) }
            .asSuccess()
    }

    override fun findDocumentByBidIds(params: FindDocumentsByBidIdsParams): Result<FindDocumentsByBidIdsResult?, Fail> {
        val receivedBidIds = params.bids.details.toSetBy { it.id }

        val targetBids = bidRepository
            .findBy(params.cpid, params.ocid).onFailure { return it }
            .filter { it.bidId in receivedBidIds }
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }

        val targetIdsIds = targetBids.toSetBy { BidId.fromString(it.id) }

        if (!targetIdsIds.containsAll(receivedBidIds))
            return FindDocumentsByBidIdsErrors.BidsNotFound(receivedBidIds - targetIdsIds).asFailure()

        val bidsDetails = targetBids
            .mapResult { FindDocumentsByBidIdsResult.Bids.Detail.fromDomain(it) }.onFailure { return it }
            .filter { it.documents.isNotEmpty() } // No need show bids without documents in response

        if (bidsDetails.isEmpty())
            return null.asSuccess()
        else
            return bidsDetails
                .let { FindDocumentsByBidIdsResult.Bids(details = it) }
                .let { FindDocumentsByBidIdsResult(it) }
                .asSuccess()
    }

    override fun getSuppliersOwners(params: GetSuppliersOwnersParams): Result<GetSuppliersOwnersResponse, Fail> {
        val bidEntities = bidRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .associateBy { it.bidId }

        if (bidEntities.isEmpty())
            return GetSuppliersOwnersErrors.BidsNotFound(params.cpid, params.ocid).asFailure()

        val receivedSuppliersIds = params.contracts.first().suppliers.toSetBy { it.id }

        val bid = bidEntities
            .map { (_, entity) ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }
            .firstOrNull { bid ->
                receivedSuppliersIds.containsAll(bid.tenderers.toSetBy { it.id })
            }
            ?: return GetSuppliersOwnersErrors.BidNotFound().asFailure()

        return GetSuppliersOwnersResponse(
            tenderers = GetSuppliersOwnersResponse.Tenderer(
                owner = bidEntities.getValue(BidId.fromString(bid.id)).owner,
                organizations = bid.tenderers
                    .map { tenderer ->
                        GetSuppliersOwnersResponse.Tenderer.Organization(
                            id = tenderer.id!!,
                            name = tenderer.name
                        )
                    }
            ).let { listOf(it) }
        ).asSuccess()
    }
}