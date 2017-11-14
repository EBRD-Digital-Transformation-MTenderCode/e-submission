package com.procurement.submission.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.submission.converter.PeriodDataDtoToPeriodEntity;
import com.procurement.submission.converter.QualificationOfferDtoToBidEntity;
import com.procurement.submission.utils.JsonUtil;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

@Configuration
@ComponentScan(basePackages = "com.procurement.submission.service")
public class ServiceConfig {

    @Bean
    public ConversionServiceFactoryBean conversionService() {
        final Set<Converter> converters = new HashSet<>();
        converters.add(new PeriodDataDtoToPeriodEntity());
        converters.add(new QualificationOfferDtoToBidEntity(jsonUtil()));
        final ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.setConverters(converters);
        return bean;
    }

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
