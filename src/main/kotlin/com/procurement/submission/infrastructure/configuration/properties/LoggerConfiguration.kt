package com.procurement.submission.infrastructure.configuration.properties

import com.procurement.submission.application.service.Logger
import com.procurement.submission.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}