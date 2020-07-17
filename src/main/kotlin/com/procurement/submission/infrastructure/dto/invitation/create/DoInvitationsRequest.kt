package com.procurement.submission.infrastructure.dto.invitation.create


import com.fasterxml.jackson.annotation.JsonProperty

data class DoInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>,
    @param:JsonProperty("submissions") @field:JsonProperty("submissions") val submissions: Submissions
) {
    data class Qualification(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
        @param:JsonProperty("relatedSubmission") @field:JsonProperty("relatedSubmission") val relatedSubmission: String
    )

    data class Submissions(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("candidates") @field:JsonProperty("candidates") val candidates: List<Candidate>
        ) {
            data class Candidate(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }
}