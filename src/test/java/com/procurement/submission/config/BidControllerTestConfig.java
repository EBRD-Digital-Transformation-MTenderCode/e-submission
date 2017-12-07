package com.procurement.submission.config;

import com.procurement.submission.controller.BidController;
import com.procurement.submission.controller.ControllerExceptionHandler;
import com.procurement.submission.service.BidService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebMvc
public class BidControllerTestConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public BidService bidService() {
        return mock(BidService.class);
    }

    @Bean
    public BidController bidController() {
        return new BidController(bidService());
    }

    @Bean
    public ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler();
    }
}
