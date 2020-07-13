package com.procurement.submission.infrastructure.configuration

import com.procurement.submission.infrastructure.configuration.properties.LoggerConfiguration
import com.procurement.submission.infrastructure.configuration.properties.TransformConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    DaoConfiguration::class,
    ServiceConfig::class,
    WebConfig::class,
    ObjectMapperConfig::class,
    TransformConfiguration::class,
    LoggerConfiguration::class
)
class ApplicationConfig
