package com.procurement.submission.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.repository.InvitationRepository
import com.procurement.submission.domain.extension.format
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class InvitationServiceTest {

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!

        private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT)
        private val DATE = LocalDateTime.parse("2020-02-10T08:49:55Z", FORMATTER)
    }

    val invitationRepository: InvitationRepository = mock()
    val generationService: GenerationService = mock()
    val invitationService = InvitationServiceImpl(invitationRepository, generationService)

    @Nested
    inner class DoInvitations {

        @Test
        fun activeQualification_noInvitationCreatedOrUpdated_success() {
            val params: DoInvitationsParams = getParams(QualificationStatusDetails.ACTIVE)

            val invitation = stubInvitation(
                status = InvitationStatus.PENDING,
                relatedQualification = params.qualifications.first().id
            )
            val invitationsStored = listOf(invitation)
            whenever(invitationRepository.findBy(cpid = params.cpid)).thenReturn(invitationsStored.asSuccess())

            val actual = invitationService.doInvitations(params).get

            assertTrue(actual == null)
        }

        @Test
        fun unsuccessfulQualification_noInvitationCreatedOrUpdated_success() {
            val params: DoInvitationsParams = getParams(QualificationStatusDetails.UNSUCCESSFUL)

            val invitation = stubInvitation(
                status = InvitationStatus.ACTIVE,
                relatedQualification = params.qualifications.first().id
            )
            val invitationsStored = listOf(invitation)
            whenever(invitationRepository.findBy(cpid = params.cpid)).thenReturn(invitationsStored.asSuccess())

            val actual = invitationService.doInvitations(params).get

            assertTrue(actual == null)
        }

        @Test
        fun invitationUpdated_success() {
            val params: DoInvitationsParams = getParams(QualificationStatusDetails.UNSUCCESSFUL)

            val invitation = stubInvitation(
                status = InvitationStatus.PENDING,
                relatedQualification = params.qualifications.first().id
            )
            val invitationsStored = listOf(invitation)
            whenever(invitationRepository.findBy(cpid = params.cpid)).thenReturn(invitationsStored.asSuccess())

            val actual = invitationService.doInvitations(params).get

            val expected = DoInvitationsResult(
                invitations = listOf(
                    DoInvitationsResult.Invitation(
                        id = invitation.id,
                        date = invitation.date,
                        status = InvitationStatus.CANCELLED,
                        relatedQualification = invitation.relatedQualification,
                        tenderers = invitation.tenderers.map { tenderer ->
                            DoInvitationsResult.Invitation.Tenderer(
                                id = tenderer.id,
                                name = tenderer.name
                            )
                        }
                    )
                )
            )
            assertEquals(expected, actual)
        }

        @Test
        fun invitationCreated_success() {
            val params: DoInvitationsParams = getParams(QualificationStatusDetails.ACTIVE)

            val invitation = stubInvitation(
                status = InvitationStatus.CANCELLED,
                relatedQualification = params.qualifications.first().id
            )
            val invitationsStored = listOf(invitation)
            whenever(invitationRepository.findBy(cpid = params.cpid)).thenReturn(invitationsStored.asSuccess())

            val invitationId = InvitationId.generate()
            whenever(generationService.generateInvitationId()).thenReturn(invitationId)

            val actual = invitationService.doInvitations(params).get

            val expected = DoInvitationsResult(
                invitations = listOf(
                    DoInvitationsResult.Invitation(
                        id = invitationId,
                        date = params.date,
                        status = InvitationStatus.PENDING,
                        relatedQualification = params.qualifications.first().id,
                        tenderers = params.submissions.details.first().candidates.map { candidate ->
                            DoInvitationsResult.Invitation.Tenderer(
                                id = candidate.id,
                                name = candidate.name
                            )
                        }
                    )
                )
            )
            assertEquals(expected, actual)
        }

        @Test
        fun missingSubmissionLinkedToQualification_fail() {
            val paramsWithWrongRelatedSubmission: DoInvitationsParams = getParamsWithWrongRelatedSubmission(
                QualificationStatusDetails.ACTIVE
            )

            val actual = invitationService.doInvitations(paramsWithWrongRelatedSubmission).error

            val expectedErrorCode = "VR.COM-13.1.2"
            val expectedErrorDescription = "Missing submission(s) by id(s) '${paramsWithWrongRelatedSubmission.qualifications.first().relatedSubmission}'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        private fun getParams(statusDetails: QualificationStatusDetails): DoInvitationsParams {
            val submissionId = SubmissionId.generate().toString()

            return DoInvitationsParams.tryCreate(
                cpid = CPID.toString(),
                date = DATE.format(),
                submissions = DoInvitationsParams.Submissions.tryCreate(
                    details = listOf(
                        DoInvitationsParams.Submissions.Detail.tryCreate(
                            id = submissionId,
                            candidates = listOf(
                                DoInvitationsParams.Submissions.Detail.Candidate(
                                    id = "candidate.id",
                                    name = "candidate.name"
                                )
                            )
                        ).get
                    )
                ).get,
                qualifications = listOf(
                    DoInvitationsParams.Qualification.tryCreate(
                        id = QualificationId.generate().toString(),
                        statusDetails = statusDetails.key,
                        relatedSubmission = submissionId
                    ).get
                )
            ).get
        }

        private fun getParamsWithWrongRelatedSubmission(statusDetails: QualificationStatusDetails) = DoInvitationsParams.tryCreate(
            cpid = CPID.toString(),
            date = DATE.format(),
            submissions = DoInvitationsParams.Submissions.tryCreate(
                details = listOf(
                    DoInvitationsParams.Submissions.Detail.tryCreate(
                        id = SubmissionId.generate().toString(),
                        candidates = listOf(
                            DoInvitationsParams.Submissions.Detail.Candidate(
                                id = "candidate.id",
                                name = "candidate.name"
                            )
                        )
                    ).get
                )
            ).get,
            qualifications = listOf(
                DoInvitationsParams.Qualification.tryCreate(
                    id = QualificationId.generate().toString(),
                    statusDetails = statusDetails.key,
                    relatedSubmission = SubmissionId.generate().toString()
                ).get
            )
        ).get

        private fun stubInvitation(status: InvitationStatus, relatedQualification: QualificationId) =
            Invitation(
                id = InvitationId.generate(),
                relatedQualification = relatedQualification,
                date = DATE,
                tenderers = listOf(
                    Invitation.Tenderer(
                        id = "tender.id",
                        name = "tender.name"
                    )
                ),
                status = status
            )
    }
}