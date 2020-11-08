package com.procurement.submission.infrastructure.repository.invitation

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.invitation.InvitationRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.entity.InvitationEntity
import org.springframework.stereotype.Repository

@Repository
class InvitationRepositoryCassandra(private val session: Session, private val transform: Transform) :
    InvitationRepository {

    companion object {

        private const val SAVE_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.Invitation.TABLE}
               (
                      ${Database.Invitation.CPID},
                      ${Database.Invitation.ID},
                      ${Database.Invitation.JSON_DATA}
               )
               VALUES(?, ?, ?)
            """

        private const val FIND_BY_CPID_CQL = """
               SELECT ${Database.Invitation.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Invitation.TABLE}
                WHERE ${Database.Invitation.CPID}=?
            """
    }

    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)

    override fun findBy(cpid: Cpid): Result<List<Invitation>, Fail.Incident> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(Database.Invitation.CPID, cpid.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .map { row -> row.convert().onFailure { return it } }
            .asSuccess()
    }

    private fun Row.convert(): Result<Invitation, Fail.Incident> {
        val data = getString(Database.Invitation.JSON_DATA)
        val entity = transform.tryDeserialization(value = data, target = InvitationEntity::class.java)
            .onFailure {
                return Fail.Incident.Database.DatabaseParsing(exception = it.reason.exception).asFailure()
            }

        return Invitation(
            id = entity.id,
            date = entity.date,
            status = entity.status,
            relatedQualification = entity.relatedQualification,
            tenderers = entity.tenderers
                .map { tenderer ->
                    Invitation.Tenderer(
                        id = tenderer.id,
                        name = tenderer.name
                    )
                }
        ).asSuccess()
    }

    override fun save(cpid: Cpid, invitation: Invitation): MaybeFail<Fail.Incident> {
        val data = generateJsonData(invitation)
            .onFailure { return MaybeFail.fail(it.reason) }

        val statements = preparedSaveCQL.bind()
            .apply {
                setString(Database.Invitation.CPID, cpid.toString())
                setString(Database.Invitation.ID, invitation.id.toString())
                setString(Database.Invitation.JSON_DATA, data)
            }

        statements.tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }

    private fun generateJsonData(invitation: Invitation): Result<String, Fail.Incident> {
        val entity = convert(invitation)
        return transform.trySerialization(entity)
            .mapFailure {
                Fail.Incident.Database.DatabaseParsing(exception = it.exception)
            }
    }

    private fun convert(invitation: Invitation) = InvitationEntity(
        id = invitation.id,
        date = invitation.date,
        status = invitation.status,
        relatedQualification = invitation.relatedQualification,
        tenderers = invitation.tenderers
            .map { tenderer ->
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
                .onFailure { return MaybeFail.fail(it.reason) }

            statement.add(
                preparedSaveCQL.bind()
                    .apply {
                        setString(Database.Invitation.CPID, cpid.toString())
                        setString(Database.Invitation.ID, invitation.id.toString())
                        setString(Database.Invitation.JSON_DATA, data)
                    }
            )
        }

        statement.tryExecute(session).onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }
}
