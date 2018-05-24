package com.procurement.submission

import com.procurement.submission.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
@EnableEurekaClient
class SubmissionApplication

fun main(args: Array<String>) {
    runApplication<SubmissionApplication>(*args)
}
