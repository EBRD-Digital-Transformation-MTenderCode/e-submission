package com.procurement.submission.infrastructure.dto.invitation.create

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateInvitationsResult
import org.junit.jupiter.api.Test

class CreateInvitationsResultTest : AbstractDTOTestBase<CreateInvitationsResult>(CreateInvitationsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/create/result_create_invitations_full.json")
    }

}