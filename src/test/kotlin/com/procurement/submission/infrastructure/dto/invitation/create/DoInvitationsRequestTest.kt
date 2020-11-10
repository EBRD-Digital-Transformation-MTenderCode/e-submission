package com.procurement.submission.infrastructure.dto.invitation.create

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.DoInvitationsRequest
import org.junit.jupiter.api.Test

class DoInvitationsRequestTest :
    AbstractDTOTestBase<DoInvitationsRequest>(DoInvitationsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/create/request_do_invitation_full.json")
    }

    @Test
    fun required() {
        testBindingAndMapping("json/infrastructure/dto/invitation/create/request_do_invitation_required.json")
    }
}
