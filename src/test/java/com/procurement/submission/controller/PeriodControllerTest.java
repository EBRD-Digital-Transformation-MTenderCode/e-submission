package com.procurement.submission.controller;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.service.PeriodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PeriodControllerTest {
//    @Test
//    @DisplayName("Test /period/check status: 200 - Ok")
//    void checkPeriodStatusOk() throws Exception {
//        final PeriodService periodService = mock(PeriodService.class);
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PeriodController(periodService))
//                                         .build();
//        mockMvc.perform(post("/period/check")
//                            .content(new JsonUtil().getResource("json/period-data.json"))
//                            .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("Test /period/check status: 400 - Bad Request")
//    void checkPeriodBadRequest() throws Exception {
//        final PeriodService periodService = mock(PeriodService.class);
//        ControllerExceptionHandler handler = new ControllerExceptionHandler();
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PeriodController(periodService))
//                                         .setControllerAdvice(handler)
//                                         .build();
//        mockMvc.perform(post("/period/check")
//                            .content("{ }")
//                            .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isBadRequest())
//               .andExpect(jsonPath("$.message").value("Something went wrong"));
//    }
//
//    @Test
//    @DisplayName("Test /period/save status: 201 - Created")
//    void savePeriodStatusCreated() throws Exception {
//        final PeriodService periodService = mock(PeriodService.class);
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PeriodController(periodService))
//                                         .build();
//        mockMvc.perform(post("/period/save")
//                            .content(new JsonUtil().getResource("json/period-data.json"))
//                            .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isCreated());
//    }
//
//    @Test
//    @DisplayName("Test /period/save status: 400 - Bad Request")
//    void savePeriodBadRequest() throws Exception {
//        final PeriodService periodService = mock(PeriodService.class);
//        ControllerExceptionHandler handler = new ControllerExceptionHandler();
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PeriodController(periodService))
//                                         .setControllerAdvice(handler)
//                                         .build();
//        mockMvc.perform(post("/period/save")
//                            .content("{ }")
//                            .contentType(MediaType.APPLICATION_JSON))
//               .andExpect(status().isBadRequest())
//               .andExpect(jsonPath("$.message").value("Something went wrong"));
//    }

}