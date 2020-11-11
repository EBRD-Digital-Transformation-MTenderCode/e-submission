package com.procurement.submission.infrastructure.extension.cassandra

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Result.Companion.failure
import com.procurement.submission.lib.functional.Result.Companion.success

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.Interaction> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.Database.Interaction(exception = expected))
}

fun BatchStatement.tryExecute(session: Session): Result<ResultSet, Fail.Incident.Database.Interaction> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Fail.Incident.Database.Interaction(exception = expected))
}
