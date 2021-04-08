package com.procurement.submission.infrastructure.dto.invitation.create

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.CreateInvitationsRequest
import org.junit.jupiter.api.Test

class CreateInvitationsRequestTest :
    AbstractDTOTestBase<CreateInvitationsRequest>(CreateInvitationsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/create/request_create_invitations_full.json")
    }
}
