package com.procurement.submission.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.submission.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackages = "com.procurement.submission.service")
public class ServiceConfig {

    @Bean
    public JsonUtil jsonUtil() {
        return new JsonUtil(objectMapper());
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper jackson2ObjectMapper = new ObjectMapper();
        jackson2ObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jackson2ObjectMapper;
    }

}
