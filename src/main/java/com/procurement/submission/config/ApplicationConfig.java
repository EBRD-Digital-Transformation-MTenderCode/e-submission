package com.procurement.submission.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.submission.utils.DateUtil;
import com.procurement.submission.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        WebConfig.class,
        ServiceConfig.class,
        DatabaseConfig.class,
        ConverterConfig.class
})
public class ApplicationConfig {
    @Bean
    public JsonUtil jsonUtil(final ObjectMapper mapper) {
        return new JsonUtil(mapper);
    }

    @Bean
    public DateUtil dateUtil() {
        return new DateUtil();
    }
}
