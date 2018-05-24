package com.procurement.submission.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("com.procurement.submission.service"))
class ServiceConfig
