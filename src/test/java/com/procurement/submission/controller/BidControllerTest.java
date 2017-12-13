package com.procurement.submission.controller;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.config.BidControllerTestConfig;
import com.procurement.submission.model.dto.request.BidStatus;
import com.procurement.submission.model.dto.request.BidsParamDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.BidsResponse;
import com.procurement.submission.service.BidService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BidControllerTestConfig.class)
@WebAppConfiguration
class BidControllerTest {
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private BidService bidService;

    private MockMvc mockMvc;

    @BeforeEach
    void initAll() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                                 .build();
    }

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 201 - Created")
    void saveQualificationProposalStatusCreated() throws Exception {
        doNothing().when(bidService)
                   .insertData(any());
        mockMvc.perform(post("/submission/qualificationOffer")
                            .content(new JsonUtil().getResource("json/qualification-offer.json"))
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 400 - Bad Request")
    void saveQualificationProposalBadRequest() throws Exception {
        mockMvc.perform(post("/submission/qualificationOffer")
                            .content("{ }")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    @DisplayName("Test url: /submission/technicalProposal status: 201 - Created")
    void testSubmissionTechnicalProposalCreated() throws Exception {
        mockMvc.perform(post("/submission/technicalProposal")
                            .contentType(APPLICATION_JSON)
                            .content(new JsonUtil().getResource("json/DocumentDto.json")))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test without core fields, url: /submission/technicalProposal status: 400 - Bad Request")
    void testSubmissionTechnicalProposalBadRequestWithoutCoreFields() throws Exception {
        mockMvc.perform(post("/submission/technicalProposal")
                            .contentType(APPLICATION_JSON)
                            .content("{ }"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"))
               .andExpect(jsonPath("$..errors.length()").value(1))
               .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("id")));
    }

    @Test
    @DisplayName("Test url: /submission/priceOffer status: 201 - Created")
    void testSubmissionPriceProposalCreated() throws Exception {
        mockMvc.perform(post("/submission/priceOffer")
                            .contentType(APPLICATION_JSON)
                            .content(new JsonUtil().getResource("json/ValueDto.json")))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test without core fields, url: /submission/priceOffer status: 400 - Bad Request")
    void testSubmissionPriceProposalBadRequestWithoutCoreFields() throws Exception {
        mockMvc.perform(post("/submission/priceOffer")
                            .contentType(APPLICATION_JSON)
                            .content("{ }"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"))
               .andExpect(jsonPath("$..errors.length()").value(2))
               .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("amount", "currency")));
    }

    @Test
    @DisplayName("Test get url: /submission/bids - 200 - Ok")
    void testGetBidsOk() throws Exception {
        final BidsResponse bids = createBids();
        when(bidService.getBids(any(BidsParamDto.class))).thenReturn(bids);
        mockMvc.perform(get(
            "/submission/bids?ocid=ocds-213czf-000-00001&procurementMethodDetail=method&stage=st&country=UA")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.bids.length()").value(2))
               .andExpect(jsonPath("$.bids").isArray())
               .andExpect(jsonPath("$.bids[0].bidId").value("id"))
               .andExpect(jsonPath("$.bids[1].bidId").value("id"))
               .andExpect(jsonPath("$.bids[0].tenderers[0].id").value("tendererId"))
               .andExpect(jsonPath("$.bids[1].tenderers[0].id").value("tendererId"))
               .andExpect(jsonPath("$.bids[0].tenderers[0].scheme").value("tendererScheme"))
               .andExpect(jsonPath("$.bids[1].tenderers[0].scheme").value("tendererScheme"))
               .andExpect(jsonPath("$.bids[0].relatedLots[0]").value("relatedLotId"))
               .andExpect(jsonPath("$.bids[1].relatedLots[0]").value("relatedLotId"));
    }

    @Test
    @DisplayName("Test without parameters get url: /submission/bids - 400 - Bad Request")
    void testGetBidsBadRequest() throws Exception {
        mockMvc.perform(get("/submission/bids")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Required String parameter 'ocid' is not present"));
        mockMvc.perform(get("/submission/bids?ocid=qwerty")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message")
                              .value("Required String parameter 'procurementMethodDetail' is not present"));
        mockMvc.perform(get("/submission/bids?ocid=ocds-213czf-000-00001&procurementMethodDetail=method")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Required String parameter 'stage' is not present"));
        mockMvc.perform(get("/submission/bids?ocid=qwerty&procurementMethodDetail=method&stage=stage")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Required String parameter 'country' is not present"));
    }

    @Test
    @DisplayName("Test with null parameters get url: /submission/bids - 400 - Bad Request")
    void testGetBidsWithNullBadRequest() throws Exception {
        mockMvc.perform(get("/submission/bids?ocid=aa&procurementMethodDetail=&stage=&country=")
                            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    @DisplayName("patch /submission/bids - 200 Ok")
    public void testPatchBidsOk() throws Exception {
        doNothing().when(bidService)
                   .patchBids(anyString(), anyString(), anyList());
        mockMvc.perform(patch("/submission/bids?ocid=ocds-213czf-000-00001&stage=stage")
                            .contentType(APPLICATION_JSON)
                            .content(new JsonUtil().getResource("json/BidAqpDtos.json")))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("patch /submission/bids - 400 BadRequest")
    public void testPatchBidsWithoutParamBadRequest() throws Exception {
        mockMvc.perform(patch("/submission/bids?ocid=&stage=")
                            .contentType(APPLICATION_JSON)
                            .content("[ ]"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"))
               .andExpect(jsonPath("$..errors.length()").value(3))
               .andExpect(jsonPath("$.errors[*].field",
                                   containsInAnyOrder("patchBids.bidAqpDtos", "patchBids.ocid", "patchBids.stage")));
    }

    @Test
    @DisplayName("patch /submission/bids - 400 BadRequest")
    public void testPatchBidsWithNotValidParamBadRequest() throws Exception {
        mockMvc.perform(patch("/submission/bids?ocid=ocds-213czf-000-0000&stage= ")
                            .contentType(APPLICATION_JSON)
                            .content(new JsonUtil().getResource("json/BidAqpDtos.json")))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Something went wrong"))
               .andExpect(jsonPath("$..errors.length()").value(2))
               .andExpect(jsonPath("$.errors[*].field",
                                   containsInAnyOrder("patchBids.stage", "patchBids.ocid")))
               .andExpect(jsonPath("$.errors[*].message",
                                   containsInAnyOrder("must not be blank", "size must be between 21 and 21")));
    }

    private BidsResponse createBids() {
        return new BidsResponse(createBidResponses());
    }

    private List<BidResponse> createBidResponses() {
        List<BidResponse> bidResponses = new ArrayList<>();
        bidResponses.add(createBidResponse());
        bidResponses.add(createBidResponse());
        return bidResponses;
    }

    private BidResponse createBidResponse() {
        return new BidResponse("id", LocalDateTime.now(), BidStatus.INVITED, createTenderers(), createRelatedLots());
    }

    private List<String> createRelatedLots() {
        List<String> relatedLots = new ArrayList<>();
        relatedLots.add("relatedLotId");
        return relatedLots;
    }

    private List<BidResponse.Tenderer> createTenderers() {
        List<BidResponse.Tenderer> tenderers = new ArrayList<>();
        tenderers.add(new BidResponse.Tenderer("tendererId", "tenderName", "tendererScheme"));
        return tenderers;
    }
}