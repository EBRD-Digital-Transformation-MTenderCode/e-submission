package com.procurement.submission.infrastructure.dto.invitation.check

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.invitation.create.CheckAbsenceActiveInvitationsRequest
import org.junit.jupiter.api.Test

class CheckAbsenceActiveInvitationsRequestTest :
    AbstractDTOTestBase<CheckAbsenceActiveInvitationsRequest>(CheckAbsenceActiveInvitationsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/check/request_check_absence_active_Invitations_full.json")
    }
}
