package com.procurement.submission.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(CassandraConfig::class, ServiceConfig::class, WebConfig::class)
class ApplicationConfig
