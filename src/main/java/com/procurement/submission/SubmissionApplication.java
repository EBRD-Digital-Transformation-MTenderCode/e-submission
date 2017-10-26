package com.procurement.submission;

import com.procurement.submission.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(
    scanBasePackageClasses = ApplicationConfig.class,
    exclude = LiquibaseAutoConfiguration.class
)
public class SubmissionApplication {
    public static void main(final String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }
}
