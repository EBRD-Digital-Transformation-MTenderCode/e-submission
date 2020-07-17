package com.procurement.submission.infrastructure.dto.invitation.entity

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.model.entity.InvitationEntity
import org.junit.jupiter.api.Test

class InvitationEntityTest :
    AbstractDTOTestBase<InvitationEntity>(InvitationEntity::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/invitation/entity/invitation_entity_full.json")
    }
}
