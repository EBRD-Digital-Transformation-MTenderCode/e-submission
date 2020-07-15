package com.procurement.submission.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.submission.application.service.Transform
import com.procurement.submission.infrastructure.service.JacksonJsonTransform
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransformConfiguration(private val objectMapper: ObjectMapper) {

    @Bean
    fun transform(): Transform = JacksonJsonTransform(mapper = objectMapper)
}
