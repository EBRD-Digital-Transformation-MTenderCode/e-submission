package com.procurement.submission.domain.fail.error

import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.fail.Fail

class BadRequest(override val description: String = "Invalid json", val exception: Exception? = null) : Fail.Error("RQ-") {
    private val numberError = "1"
    override val code: String = "${prefix}${numberError}"

    override fun logging(logger: Logger) {
        logger.error(message = "$code. $description", exception = exception)
    }
}
