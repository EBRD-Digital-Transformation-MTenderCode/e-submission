package com.procurement.submission.infrastructure.dto.invitation.publish

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.PublishInvitationsRequest
import org.junit.jupiter.api.Test

class PublishInvitationsRequestTest :
    AbstractDTOTestBase<PublishInvitationsRequest>(PublishInvitationsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/publish/publish_invitation_request_full.json")
    }
}
