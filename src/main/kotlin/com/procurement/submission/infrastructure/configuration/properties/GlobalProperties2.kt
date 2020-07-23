package com.procurement.submission.infrastructure.configuration.properties

import com.procurement.submission.infrastructure.io.orThrow
import com.procurement.submission.infrastructure.web.api.version.ApiVersion2
import java.util.*

object GlobalProperties2 {
    val service = Service()

    object App {
        val apiVersion = ApiVersion2(major = 1, minor = 0, patch = 0)
    }

    class Service {
        val id: String = "4"
        val name: String = "e-submission"
        val version: String = loadVersion()

        private fun loadVersion(): String {
            val gitProps: Properties = try {
                GlobalProperties2::class.java.getResourceAsStream("/git.properties")
                    .use { stream ->
                        Properties().apply { load(stream) }
                    }
            } catch (expected: Exception) {
                throw IllegalStateException(expected)
            }
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}
