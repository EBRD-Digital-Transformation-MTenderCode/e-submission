package com.procurement.submission.controller;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.dto.request.BidsGetDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.service.BidService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
class BidControllerTest {

    private static BidController bidController;
    private static BidService bidService = mock(BidService.class);

    @BeforeAll
    static void initAll() {
//        final BidService bidService = mock(BidService.class);
        bidController = new BidController(bidService);
    }

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 201 - Created")
    void saveQualificationProposalStatusCreated() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                         .build();
        mockMvc.perform(post("/submission/qualificationOffer")
            .content(new JsonUtil().getResource("json/qualification-offer.json"))
            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 400 - Bad Request")
    void saveQualificationProposalBadRequest() throws Exception {
        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                         .setControllerAdvice(handler)
                                         .build();
        mockMvc.perform(post("/submission/qualificationOffer")
            .content("{ }")
            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Houston we have a problem"));
    }

    @Test
    @DisplayName("Test url: /submission/technicalProposal status: 201 - Created")
    void testSubmissionTechnicalProposalCreated() throws Exception {
        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                               .build();
        mockMvc.perform(post("/submission/technicalProposal")
            .contentType(APPLICATION_JSON)
            .content(new JsonUtil().getResource("json/DocumentDto.json")))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test without core fields, url: /submission/technicalProposal status: 400 - Bad Request")
    void testSubmissionTechnicalProposalBadRequestWithoutCoreFields() throws Exception {
        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                               .setControllerAdvice(new ControllerExceptionHandler())
                                               .build();
        mockMvc.perform(post("/submission/technicalProposal")
            .contentType(APPLICATION_JSON)
            .content("{ }"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Houston we have a problem"))
               .andExpect(jsonPath("$..errors.length()").value(1))
               .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("id")));
    }

    @Test
    @DisplayName("Test url: /submission/priceOffer status: 201 - Created")
    void testSubmissionPriceProposalCreated() throws Exception {
        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                               .build();
        mockMvc.perform(post("/submission/priceOffer")
            .contentType(APPLICATION_JSON)
            .content(new JsonUtil().getResource("json/ValueDto.json")))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test without core fields, url: /submission/priceOffer status: 400 - Bad Request")
    void testSubmissionPriceProposalBadRequestWithoutCoreFields() throws Exception {
        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                               .setControllerAdvice(new ControllerExceptionHandler())
                                               .build();
        mockMvc.perform(post("/submission/priceOffer")
            .contentType(APPLICATION_JSON)
            .content("{ }"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Houston we have a problem"))
               .andExpect(jsonPath("$..errors.length()").value(2))
               .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("amount", "currency")));
    }

    @Test
    @DisplayName("Test url: /submission/bids - 200 - Ok")
    void testGetBids() throws Exception {
        final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bidController)
                                               .build();
        List<BidResponse> bidResponses = createBidResponses();
        when(bidService.getBids(any(BidsGetDto.class))).thenReturn(bidResponses);
        mockMvc.perform(get("/submission/bids?ocid=qwre&procurementMethodDetail=method&stage=st&country=UA")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.[].length()").value(4));
    }

    private List<BidResponse> createBidResponses() {
        List<BidResponse> bidResponses = new ArrayList<>();
        bidResponses.add(createBidResponse());
        return bidResponses;
    }

    private BidResponse createBidResponse() {
        return new BidResponse("id", createTenderers(), createRelatedLots());
    }

    private List<BidResponse.RelatedLot> createRelatedLots() {
        List<BidResponse.RelatedLot> relatedLots = new ArrayList<>();
        relatedLots.add(new BidResponse.RelatedLot("relatedLotId"));
        return relatedLots;
    }

    private List<BidResponse.Tenderer> createTenderers() {
        List<BidResponse.Tenderer> tenderers = new ArrayList<>();
        tenderers.add(new BidResponse.Tenderer("tendererId", "tendererScheme"));
        return tenderers;
    }
}