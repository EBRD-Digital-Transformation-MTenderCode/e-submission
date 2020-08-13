package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsRequest

fun DoInvitationsRequest.convert() = DoInvitationsParams.tryCreate(
    cpid = cpid,
    date = date,
    submissions = submissions?.convert()?.orForwardFail { fail -> return fail },
    qualifications = qualifications?.mapResult { it.convert() }?.orForwardFail { fail -> return fail }
)

fun DoInvitationsRequest.Qualification.convert() = DoInvitationsParams.Qualification.tryCreate(
    id = id,
    relatedSubmission = relatedSubmission,
    statusDetails = statusDetails
)

fun DoInvitationsRequest.Submissions.convert() = DoInvitationsParams.Submissions.tryCreate(
    details = details.mapResult { it.convert() }.orForwardFail { fail -> return fail }
)

fun DoInvitationsRequest.Submissions.Detail.convert() = DoInvitationsParams.Submissions.Detail.tryCreate(
    id = id,
    candidates = candidates.map { candidate ->
        DoInvitationsParams.Submissions.Detail.Candidate( id = candidate.id, name = candidate.name)
    }
)