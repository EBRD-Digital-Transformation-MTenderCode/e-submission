package com.procurement.submission.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = "com.procurement.submission.controller")
public class WebConfig {
}
