package com.procurement.submission.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.BidRepository
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.enums.Stage
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.entity.BidEntityComplex
import org.springframework.stereotype.Repository

@Repository
class BidRepositoryCassandra(private val session: Session, private val transform: Transform) :
    BidRepository {

    companion object {
        private const val KEYSPACE = "ocds"
        private const val BID_TABLE = "submission_bid"
        private const val CPID_COLUMN = "cp_id"
        private const val STAGE_COLUMN = "stage"
        private const val BID_ID_COLUMN = "bid_id"
        private const val TOKEN_COLUMN = "token_entity"
        private const val OWNER_COLUMN = "owner"
        private const val STATUS_COLUMN = "status"
        private const val CREATED_DATE_COLUMN = "created_date"
        private const val PENDING_DATE_COLUMN = "pending_date"
        private const val JSON_DATA_COLUMN = "json_data"

        private const val FIND_BY_CQL = """
               SELECT $CPID_COLUMN,
                      $STAGE_COLUMN,
                      $BID_ID_COLUMN,
                      $TOKEN_COLUMN,
                      $OWNER_COLUMN,
                      $STATUS_COLUMN,
                      $CREATED_DATE_COLUMN,
                      $PENDING_DATE_COLUMN,
                      $JSON_DATA_COLUMN
                 FROM $KEYSPACE.$BID_TABLE
                WHERE $CPID_COLUMN=?
                  AND $STAGE_COLUMN=?
            """

        private const val SAVE_CQL = """
               INSERT INTO $KEYSPACE.$BID_TABLE(
                      $CPID_COLUMN,
                      $STAGE_COLUMN,
                      $BID_ID_COLUMN,
                      $TOKEN_COLUMN,
                      $OWNER_COLUMN,
                      $STATUS_COLUMN,
                      $CREATED_DATE_COLUMN,
                      $PENDING_DATE_COLUMN,
                      $JSON_DATA_COLUMN
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
    }

    private val preparedFindByCQL = session.prepare(FIND_BY_CQL)
    private val preparedSaveCQL = session.prepare(SAVE_CQL)

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntityComplex>, Fail.Incident> {
        val query = preparedFindByCQL.bind()
            .apply {
                setString(CPID_COLUMN, cpid.toString())
                setString(STAGE_COLUMN, ocid.stage.toString())
            }

        return query.tryExecute(session)
            .orForwardFail { fail -> return fail }
            .map { row -> row.convert().orForwardFail { fail -> return fail } }
            .asSuccess()
    }

    private fun Row.convert(): Result<BidEntityComplex, Fail.Incident> {
        val data = getString(JSON_DATA_COLUMN)
        val entity = transform.tryDeserialization(value = data, target = Bid::class.java)
            .doReturn { fail ->
                return Fail.Incident.Database.DatabaseParsing(exception = fail.exception).asFailure()
            }

        return BidEntityComplex(
            cpid = Cpid.tryCreateOrNull(getString(CPID_COLUMN))!!,
            bidId = getUUID(BID_ID_COLUMN),
            token = getUUID(TOKEN_COLUMN),
            stage = Stage.creator(getString(STAGE_COLUMN)),
            owner = Owner.fromString(getString(OWNER_COLUMN)),
            status = Status.creator(getString(STATUS_COLUMN)),
            createdDate = getTimestamp(CREATED_DATE_COLUMN),
            pendingDate = getTimestamp(PENDING_DATE_COLUMN),
            bid = entity
        ).asSuccess()
    }

    override fun saveAll(bidEntities: List<BidEntityComplex>): MaybeFail<Fail.Incident> {
        val statement = BatchStatement()

        bidEntities.forEach { bidEntity ->
            val data = generateJsonData(bidEntity.bid)
                .doReturn { fail -> return MaybeFail.fail(fail) }

            statement.add(
                preparedSaveCQL.bind()
                    .apply {
                        setString(CPID_COLUMN, bidEntity.cpid.toString())
                        setString(STAGE_COLUMN, bidEntity.stage.toString())
                        setUUID(BID_ID_COLUMN, bidEntity.bidId)
                        setUUID(TOKEN_COLUMN, bidEntity.token)
                        setString(OWNER_COLUMN, bidEntity.owner.toString())
                        setString(STATUS_COLUMN, bidEntity.status.toString())
                        setTimestamp(CREATED_DATE_COLUMN, bidEntity.createdDate)
                        setTimestamp(PENDING_DATE_COLUMN, bidEntity.pendingDate)
                        setString(JSON_DATA_COLUMN, data)
                    }
            )
        }

        statement.tryExecute(session)
            .doOnError { fail -> return MaybeFail.fail(fail) }

        return MaybeFail.none()
    }

    private fun generateJsonData(bid: Bid): Result<String, Fail.Incident> =
        transform.trySerialization(bid)
            .doOnError { error ->
                return Fail.Incident.Database.DatabaseParsing(exception = error.exception)
                    .asFailure()
            }
}