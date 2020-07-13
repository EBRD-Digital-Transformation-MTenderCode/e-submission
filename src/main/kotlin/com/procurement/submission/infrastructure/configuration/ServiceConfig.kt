package com.procurement.submission.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.submission.infrastructure.service",
        "com.procurement.submission.application.service",
        "com.procurement.submission.infrastructure.handler"
    ]
)
class ServiceConfig
