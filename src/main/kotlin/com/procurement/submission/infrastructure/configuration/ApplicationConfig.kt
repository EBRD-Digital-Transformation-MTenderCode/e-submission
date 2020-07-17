package com.procurement.submission.infrastructure.configuration

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
