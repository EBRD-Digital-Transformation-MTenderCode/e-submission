package com.procurement.submission.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.submission.infrastructure.handler.bid.jackson.configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig(@Autowired objectMapper: ObjectMapper) {

    init {
        objectMapper.apply { configuration() }
    }
}
