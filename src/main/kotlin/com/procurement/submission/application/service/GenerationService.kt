package com.procurement.submission.application.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.submission.domain.model.invitation.InvitationId
import org.springframework.stereotype.Service
import java.util.*

@Service
class GenerationService {

    fun generateRandomUUID(): UUID {
        return UUIDs.random()
    }

    fun generateTimeBasedUUID(): UUID {
        return UUIDs.timeBased()
    }

    fun getRandomUUID(): String {
        return generateRandomUUID().toString()
    }

    fun getTimeBasedUUID(): String {
        return generateTimeBasedUUID().toString()
    }

    fun generateBidId(): UUID = UUID.randomUUID()

    fun generateRequirementResponseId(): UUID = UUID.randomUUID()

    fun generateInvitationId(): InvitationId = InvitationId.generate()
}