package com.procurement.submission;

import com.procurement.submission.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(
    scanBasePackageClasses = ApplicationConfig.class
)
@EnableEurekaClient
public class SubmissionApplication {
    public static void main(final String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }
}
