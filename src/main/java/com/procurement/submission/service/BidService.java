package com.procurement.submission.service;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.ocds.Bid;

public interface BidService {

    ResponseDto createBid(String ocId,
                          String stage,
                          String owner,
                          Bid bidDto);

    ResponseDto updateBid(String ocId,
                          String stage,
                          String token,
                          String owner,
                          Bid bidDto);

    ResponseDto copyBids(String ocId,
                         String stage,
                         String previousStage,
                         LotsDto lots);

    ResponseDto getBids(String ocId,
                        String country,
                        String pmd,
                        String stage,
                        Bid.Status status);

    ResponseDto updateBidsByLots(String ocId,
                                     String stage,
                                     String country,
                                     String pmd,
                                     LotsDto lots);

    ResponseDto updateStatusDetail(String ocId,
                                      String stage,
                                      String bidId,
                                      String awardStatus);

    ResponseDto setFinalStatuses(String ocId, String stage);
}
