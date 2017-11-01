package com.procurement.submission.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@ComponentScan(basePackages = "com.procurement.submission.model.entity")
@EnableCassandraRepositories(basePackages = "com.procurement.submission.repository")
public class DatabaseConfig {
}
