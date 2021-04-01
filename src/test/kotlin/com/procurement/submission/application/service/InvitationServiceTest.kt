package com.procurement.submission.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.domain.extension.format
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.failure
import com.procurement.submission.get
import com.procurement.submission.infrastructure.handler.v2.model.response.DoInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.PublishInvitationsResult
import com.procurement.submission.lib.functional.Result.Companion.success
import com.procurement.submission.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.*

class InvitationServiceTest {

    companion object {
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!
        private val OPERATION_TYPE = OperationType.CREATE_PCR

        private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT)
        private val DATE = LocalDateTime.parse("2020-02-10T08:49:55Z", FORMATTER)
    }

    val invitationRepository: InvitationRepository = mock()
    val generationService: GenerationService = mock()
    val rulesService: RulesService = mock()
    val transform: Transform = mock()
    val bidRepository: BidRepository = mock()
    val invitationService = InvitationServiceImpl(invitationRepository, bidRepository, generationService, rulesService, transform)

    @Nested
    inner class DoInvitations {

        @Test
        fun missingSubmissionLinkedToQualification_fail() {
            val paramsWithWrongRelatedSubmission: DoInvitationsParams = getParamsWithWrongRelatedSubmission(
                QualificationStatusDetails.ACTIVE
            )

            val actual = invitationService.doInvitations(paramsWithWrongRelatedSubmission).failure()

            val expectedErrorCode = "VR.COM-13.1.2"
            val expectedErrorDescription =
                "Missing submission(s) by id(s) '${paramsWithWrongRelatedSubmission.qualifications.first().relatedSubmission}'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorDescription, actual.description)
        }

        @Test
        fun linkedPendingInvitationIsPresent_nullResult() {
            val params = getParams()

            val invitation = stubInvitation(
                status = InvitationStatus.PENDING,
                relatedQualification = params.qualifications.first().id
            )

            whenever(invitationRepository.findBy(params.cpid)).thenReturn(listOf(invitation).asSuccess())

            val actual = invitationService.doInvitations(params).get()

            assertTrue(actual == null)
        }

        @Test
        fun noPendingInvitationIsPresent_newInvitationReturned() {
            val params = getParams()

            val invitation = stubInvitation(
                status = InvitationStatus.ACTIVE,
                relatedQualification = params.qualifications.first().id
            )
            whenever(invitationRepository.findBy(params.cpid)).thenReturn(listOf(invitation).asSuccess())

            val invitationId = InvitationId.generate()
            whenever(generationService.generateInvitationId()).thenReturn(invitationId)
            whenever(
                rulesService.getReturnInvitationsFlag(params.country, params.pmd, params.operationType)
            ).thenReturn(true.asSuccess())

            val actual = invitationService.doInvitations(params).get()

            val expected = DoInvitationsResult(
                invitations = listOf(getExpectedNewInvitation(invitationId, params))
            )

            assertEquals(expected, actual)
            verify(invitationRepository).saveAll(eq(params.cpid), any())
        }

        @Test
        fun noPendingInvitationIsPresentButInvitationsReturnFlagFalse_nullResult() {
            val params = getParams()

            val invitation = stubInvitation(
                status = InvitationStatus.ACTIVE,
                relatedQualification = params.qualifications.first().id
            )
            whenever(invitationRepository.findBy(params.cpid)).thenReturn(listOf(invitation).asSuccess())

            val invitationId = InvitationId.generate()
            whenever(generationService.generateInvitationId()).thenReturn(invitationId)
            whenever(
                rulesService.getReturnInvitationsFlag(
                    params.country,
                    params.pmd,
                    params.operationType
                )
            ).thenReturn(false.asSuccess())

            val actual = invitationService.doInvitations(params).get()

            assertTrue(actual == null)
        }

        @Test
        fun unlinkedPendingInvitationIsPresent_canceledAndNewInvitationsReturned() {
            val params = getParams()

            val invitation = stubInvitation(
                status = InvitationStatus.PENDING,
                relatedQualification = QualificationId.generate()
            )
            whenever(invitationRepository.findBy(params.cpid)).thenReturn(listOf(invitation).asSuccess())

            val invitationId = InvitationId.generate()
            whenever(generationService.generateInvitationId()).thenReturn(invitationId)
            whenever(
                rulesService.getReturnInvitationsFlag(
                    params.country,
                    params.pmd,
                    params.operationType
                )
            ).thenReturn(true.asSuccess())

            val actual = invitationService.doInvitations(params).get()

            val expectedCanceledInvitation = getExpectedCanceledInvitation(invitation, params)
            val expectedNewInvitation = getExpectedNewInvitation(invitationId, params)

            val expected = DoInvitationsResult(listOf(expectedCanceledInvitation, expectedNewInvitation))

            assertEquals(expected, actual)
            verify(invitationRepository).saveAll(eq(params.cpid), any())
        }

        @Test
        fun unlinkedPendingInvitationIsPresentButInvitationsReturnFlagFalse_nullResult() {
            val params = getParams()

            val invitation = stubInvitation(
                status = InvitationStatus.PENDING,
                relatedQualification = QualificationId.generate()
            )
            whenever(invitationRepository.findBy(params.cpid)).thenReturn(listOf(invitation).asSuccess())

            val invitationId = InvitationId.generate()
            whenever(generationService.generateInvitationId()).thenReturn(invitationId)
            whenever(
                rulesService.getReturnInvitationsFlag(
                    params.country,
                    params.pmd,
                    params.operationType
                )
            ).thenReturn(false.asSuccess())

            val actual = invitationService.doInvitations(params).get()

            assertTrue(actual == null)
        }

        private fun getExpectedNewInvitation(
            invitationId: InvitationId,
            params: DoInvitationsParams
        ): DoInvitationsResult.Invitation {
            return DoInvitationsResult.Invitation(
                id = invitationId,
                date = params.date,
                status = InvitationStatus.PENDING,
                relatedQualification = params.qualifications.first().id,
                tenderers = params.submissions?.details?.first()?.candidates?.map { tenderer ->
                    DoInvitationsResult.Invitation.Tenderer(
                        id = tenderer.id,
                        name = tenderer.name
                    )
                }.orEmpty()
            )
        }

        private fun getExpectedCanceledInvitation(
            invitation: Invitation,
            params: DoInvitationsParams
        ) = DoInvitationsResult.Invitation(
            id = invitation.id,
            date = params.date,
            status = InvitationStatus.CANCELLED,
            relatedQualification = invitation.relatedQualification,
            tenderers = invitation.tenderers.map { tenderer ->
                DoInvitationsResult.Invitation.Tenderer(
                    id = tenderer.id,
                    name = tenderer.name
                )
            }
        )

        private fun getParams(): DoInvitationsParams {
            val submissionId = SubmissionId.generate().toString()

            return DoInvitationsParams.tryCreate(
                cpid = CPID.toString(),
                date = DATE.format(),
                country = "MD",
                operationType = OperationType.START_SECOND_STAGE.key,
                pmd = ProcurementMethod.GPA.name,
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
                        ).get()
                    )
                ).get(),
                qualifications = listOf(
                    DoInvitationsParams.Qualification.tryCreate(
                        id = QualificationId.generate().toString(),
                        statusDetails = QualificationStatusDetails.ACTIVE.key,
                        relatedSubmission = submissionId
                    ).get()
                )
            ).get()
        }

        private fun getParamsWithWrongRelatedSubmission(statusDetails: QualificationStatusDetails) =
            DoInvitationsParams.tryCreate(
                cpid = CPID.toString(),
                date = DATE.format(),
                country = "MD",
                operationType = OperationType.START_SECOND_STAGE.key,
                pmd = ProcurementMethod.GPA.name,
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
                        ).get()
                    )
                ).get(),
                qualifications = listOf(
                    DoInvitationsParams.Qualification.tryCreate(
                        id = QualificationId.generate().toString(),
                        statusDetails = statusDetails.key,
                        relatedSubmission = SubmissionId.generate().toString()
                    ).get()
                )
            ).get()

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

    @Nested
    inner class PublishInvitations {

        @Test
        fun pendingAndActiveInvitationStored_returnsBoth() {
            val params = getParams()
            val invitations = listOf(getInvitation(InvitationStatus.ACTIVE), getInvitation(InvitationStatus.PENDING))
            whenever(invitationRepository.findBy(cpid = params.cpid))
                .thenReturn(success(invitations))
            val actual = invitationService.publishInvitations(params).get().invitations
            val expected = invitations.map { invitation ->
                PublishInvitationsResult.Invitation(
                    id = invitation.id,
                    status = InvitationStatus.ACTIVE,
                    date = invitation.date,
                    relatedQualification = invitation.relatedQualification,
                    tenderers = invitation.tenderers.map { tenderer ->
                        PublishInvitationsResult.Invitation.Tenderers(
                            id = tenderer.id,
                            name = tenderer.name
                        )
                    }
                )
            }
            assertTrue(actual.size == 2)
            assertEquals(expected.toSet(), actual.toSet())
        }

        @Test
        fun pendingAndCancelledInvitationStored_returnsUpdatedPending() {
            val params = getParams()
            val invitations = listOf(getInvitation(InvitationStatus.CANCELLED), getInvitation(InvitationStatus.PENDING))
            whenever(invitationRepository.findBy(cpid = params.cpid))
                .thenReturn(success(invitations))
            val actual = invitationService.publishInvitations(params).get().invitations
            val expected = invitations[1].let { invitation ->
                PublishInvitationsResult.Invitation(
                    id = invitation.id,
                    status = InvitationStatus.ACTIVE,
                    date = invitation.date,
                    relatedQualification = invitation.relatedQualification,
                    tenderers = invitation.tenderers.map { tenderer ->
                        PublishInvitationsResult.Invitation.Tenderers(
                            id = tenderer.id,
                            name = tenderer.name
                        )
                    }
                )
            }
            assertTrue(actual.size == 1)
            assertEquals(expected, actual.first())
        }

        private fun getParams() =
            PublishInvitationsParams.tryCreate(cpid = CPID.toString(), operationType = OPERATION_TYPE.key).get()

        fun getInvitation(status: InvitationStatus) =
            Invitation(
                id = InvitationId.generate(),
                status = status,
                relatedQualification = QualificationId.generate(),
                date = LocalDateTime.now(),
                tenderers = listOf(
                    Invitation.Tenderer(
                        id = UUID.randomUUID().toString(),
                        name = "tenderer.name"
                    )
                )
            )
    }
}