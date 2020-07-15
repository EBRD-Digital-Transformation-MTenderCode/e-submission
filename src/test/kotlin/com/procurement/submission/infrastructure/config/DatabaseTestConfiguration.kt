package com.procurement.submission.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.submission.application.service.Transform
import com.procurement.submission.infrastructure.configuration.ObjectMapperConfig
import com.procurement.submission.infrastructure.configuration.properties.TransformConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.wait.strategy.Wait

@TestConfiguration
class DatabaseTestConfiguration {
    @Bean
    fun container() = CassandraTestContainer(
        "3.11"
    )
        .apply {
            setWaitStrategy(Wait.forListeningPort())
            start()
        }

    @Bean
    fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        ObjectMapperConfig(mapper)
        return mapper
    }

    @Bean
    fun transform(): Transform = TransformConfiguration(mapper()).transform()
}