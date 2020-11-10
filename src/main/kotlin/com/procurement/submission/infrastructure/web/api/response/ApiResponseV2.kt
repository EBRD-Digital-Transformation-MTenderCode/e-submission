package com.procurement.submission.infrastructure.web.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.model.IncidentId
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.web.api.version.ApiVersion2
import java.time.LocalDateTime

@JsonPropertyOrder("version", "id", "status", "result")
sealed class ApiResponseV2 {
    abstract val version: ApiVersion2
    abstract val id: CommandId
    abstract val status: Status
    abstract val result: Any?

    class Success(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion2,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: Any? = null
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.SUCCESS
    }

    class Error(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion2,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: List<Result>
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.ERROR

        class Result(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail> = emptyList()
        ) {

            class Detail private constructor(
                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String? = null
            ) {

                companion object {
                    fun tryCreateOrNull(id: String? = null, name: String? = null): Detail? =
                        if (id == null && name == null) null else Detail(id = id, name = name)
                }
            }
        }
    }

    class Incident(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion2,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: Result
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.INCIDENT

        class Result(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: IncidentId,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
            @field:JsonProperty("level") @param:JsonProperty("level") val level: Fail.Incident.Level,
            @field:JsonProperty("service") @param:JsonProperty("service") val service: Service,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) {

            class Service(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("version") @param:JsonProperty("version") val version: String
            )

            class Detail(
                @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("metadata") @param:JsonProperty("metadata") val metadata: Any?
            )
        }
    }

    enum class Status(@JsonValue val value: String) {
        SUCCESS("success"),
        ERROR("error"),
        INCIDENT("incident")
    }
}