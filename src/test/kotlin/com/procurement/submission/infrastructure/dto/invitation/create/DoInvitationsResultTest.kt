package com.procurement.submission.infrastructure.dto.invitation.create

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.DoInvitationsResult
import org.junit.jupiter.api.Test

class DoInvitationsResultTest :
    AbstractDTOTestBase<DoInvitationsResult>(DoInvitationsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/create/result_do_invitation_full.json")
    }
}
