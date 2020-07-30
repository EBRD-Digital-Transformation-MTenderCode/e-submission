package com.procurement.submission.domain.fail.error

sealed class DataTimeError {

    data class InvalidFormat(val value: String, val pattern: String, val exception: Exception) : DataTimeError()

    data class InvalidDateTime(val value: String, val exception: Exception) : DataTimeError()
}
