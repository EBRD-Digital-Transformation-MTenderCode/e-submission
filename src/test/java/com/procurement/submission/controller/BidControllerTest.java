package com.procurement.submission.controller;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.service.BidService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BidControllerTest {

    private static BidController bidController;

    @BeforeAll
    static void initAll() {
        final BidService bidService2 = mock(BidService.class);
        bidController = new BidController(bidService2);
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
        mockMvc.perform(get("/submission/bids?ocid=qwre&procurementMethodDetail=method&stage=st&country=UA")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk());
// FIXME: 24.11.17
//        validateMockitoUsage();
    }
}