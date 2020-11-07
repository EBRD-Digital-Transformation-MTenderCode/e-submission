package com.procurement.submission.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.submission.application.service.Transform
import com.procurement.submission.infrastructure.configuration.TransformConfiguration
import com.procurement.submission.infrastructure.handler.bid.jackson.configuration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.wait.strategy.Wait

@TestConfiguration
class DatabaseTestConfiguration {
    @Bean
    fun container() = CassandraTestContainer("3.11")
        .apply {
            setWaitStrategy(Wait.forListeningPort())
            start()
        }

    @Bean
    fun mapper(): ObjectMapper = ObjectMapper().apply { configuration() }

    @Bean
    fun transform(): Transform = TransformConfiguration(mapper()).transform()
}
