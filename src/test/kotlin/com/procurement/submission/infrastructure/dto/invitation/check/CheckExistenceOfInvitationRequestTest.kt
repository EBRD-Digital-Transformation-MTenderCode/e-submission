package com.procurement.submission.infrastructure.dto.invitation.check

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckExistenceOfInvitationRequest
import org.junit.jupiter.api.Test

class CheckExistenceOfInvitationRequestTest :
    AbstractDTOTestBase<CheckExistenceOfInvitationRequest>(CheckExistenceOfInvitationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/check/request_check_existence_of_invitation_full.json")
    }
}
