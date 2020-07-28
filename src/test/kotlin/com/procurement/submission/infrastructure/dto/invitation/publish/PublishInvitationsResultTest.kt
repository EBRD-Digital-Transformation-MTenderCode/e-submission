package com.procurement.submission.infrastructure.dto.invitation.publish

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class PublishInvitationsResultTest :
    AbstractDTOTestBase<PublishInvitationsResult>(
        PublishInvitationsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/publish/publish_invitation_result_full.json")
    }
}
