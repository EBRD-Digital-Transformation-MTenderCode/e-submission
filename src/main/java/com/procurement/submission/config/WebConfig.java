package com.procurement.submission.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
//@EnableSwagger2
@ComponentScan(basePackages = "com.procurement.submission.controller")
public class WebConfig {
}
