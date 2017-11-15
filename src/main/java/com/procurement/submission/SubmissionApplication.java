package com.procurement.submission;

import com.procurement.submission.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = ApplicationConfig.class
)
public class SubmissionApplication {
    public static void main(final String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }
}
