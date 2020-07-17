package com.procurement.submission

import com.procurement.submission.infrastructure.configuration.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
class SubmissionApplication

fun main(args: Array<String>) {
    runApplication<SubmissionApplication>(*args)
}
