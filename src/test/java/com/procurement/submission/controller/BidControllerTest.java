package com.procurement.submission.controller;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.service.BidService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BidControllerTest{

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 201 - Created")
    void saveQualificationProposalStatusCreated() throws Exception {
        final BidService bidService = mock(BidService.class);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new BidController(bidService))
                                         .build();
        mockMvc.perform(post("/submission/qualificationOffer")
                            .content(new JsonUtil().getResource("json/qualification-offer.json"))
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test /submission/qualificationOffer status: 400 - Bad Request")
    void saveQualificationProposalBadRequest() throws Exception {
        final BidService bidService = mock(BidService.class);
        ControllerExceptionHandler handler = new ControllerExceptionHandler();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new BidController(bidService))
                                         .setControllerAdvice(handler)
                                         .build();
        mockMvc.perform(post("/submission/qualificationOffer")
                            .content("{ }")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Houston we have a problem"));
    }

}