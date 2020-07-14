package com.procurement.submission.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.InvitationRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.MaybeFail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asFailure
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.model.entity.InvitationEntity
import org.springframework.stereotype.Repository

@Repository
class InvitationRepositoryCassandra(private val session: Session, private val transform: Transform) :
    InvitationRepository {

    companion object {
        private const val KEYSPACE = "ocds"
        private const val INVITATION_TABLE = "submission_invitation"
        private const val CPID_COLUMN = "cpid"
        private const val ID_COLUMN = "id"
        const val JSON_DATA_COLUMN = "json_data"

        private const val SAVE_CQL = """
               INSERT INTO $KEYSPACE.$INVITATION_TABLE(
                      $CPID_COLUMN,
                      $ID_COLUMN,
                      $JSON_DATA_COLUMN
               )
               VALUES(?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_BY_CPID_CQL = """
               SELECT $JSON_DATA_COLUMN
                 FROM $KEYSPACE.$INVITATION_TABLE
                WHERE $CPID_COLUMN=?
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)

    override fun findBy(cpid: Cpid): Result<List<Invitation>, Fail.Incident> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(CPID_COLUMN, cpid.toString())
            }

        return query.tryExecute(session)
            .orForwardFail { fail -> return fail }
            .map { row -> row.convert().orForwardFail { fail -> return fail } }
            .asSuccess()
    }

    private fun Row.convert(): Result<Invitation, Fail.Incident> {
        val data = getString(JSON_DATA_COLUMN)
        val entity = transform.tryDeserialization(value = data, target = InvitationEntity::class.java)
            .doReturn { fail ->
                return Fail.Incident.Database.DatabaseParsing(exception = fail.exception).asFailure()
            }

        return Invitation(
            id = entity.id,
            date = entity.date,
            status = entity.status,
            relatedQualification = entity.relatedQualification,
            tenderers = entity.tenderers.map { tenderer ->
                Invitation.Tenderer(
                    id = tenderer.id,
                    name = tenderer.name
                )
            }
        ).asSuccess()
    }

    override fun save(cpid: Cpid, invitation: Invitation): MaybeFail<Fail.Incident> {
        val data = generateJsonData(invitation)
            .doReturn { fail -> return MaybeFail.fail(fail) }

        val statements = preparedSaveCQL.bind()
            .apply {
                setString(CPID_COLUMN, cpid.toString())
                setString(ID_COLUMN, invitation.id.toString())
                setString(JSON_DATA_COLUMN, data)
            }

        statements.tryExecute(session)
            .doOnError { fail -> return MaybeFail.fail(fail) }

        return MaybeFail.none()
    }

    private fun generateJsonData(invitation: Invitation): Result<String, Fail.Incident> {
        val entity = convert(invitation)
        return transform.trySerialization(entity)
            .doOnError { error ->
                return Fail.Incident.Database.DatabaseParsing(exception = error.exception)
                    .asFailure()
            }
    }

    private fun convert(invitation: Invitation) = InvitationEntity(
        id = invitation.id,
        date = invitation.date,
        status = invitation.status,
        relatedQualification = invitation.relatedQualification,
        tenderers = invitation.tenderers.map { tenderer ->
            InvitationEntity.Tenderer(
                id = tenderer.id,
                name = tenderer.name
            )
        }
    )

    override fun saveAll(cpid: Cpid, invitations: List<Invitation>): MaybeFail<Fail.Incident> {

        val statement = BatchStatement()

        invitations.forEach { invitation ->
            val data = generateJsonData(invitation)
                .doReturn { fail -> return MaybeFail.fail(fail) }

            statement.add(
                preparedSaveCQL.bind()
                    .apply {
                        setString(CPID_COLUMN, cpid.toString())
                        setString(ID_COLUMN, invitation.id.toString())
                        setString(JSON_DATA_COLUMN, data)
                    }
            )
        }

        statement.tryExecute(session)
            .doOnError { fail -> return MaybeFail.fail(fail) }

        return MaybeFail.none()
    }
}