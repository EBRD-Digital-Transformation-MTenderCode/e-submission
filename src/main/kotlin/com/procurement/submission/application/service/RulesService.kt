package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.repository.rule.RuleRepository
import com.procurement.submission.domain.extension.tryToBoolean
import com.procurement.submission.domain.extension.tryToLong
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.rule.BidStatesRule
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RulesService(
    private val ruleRepository: RuleRepository,
    private val transform: Transform
) {

    fun getInterval(country: String, pmd: ProcurementMethod): Duration =
        ruleRepository.find(country, pmd, PARAMETER_INTERVAL)
            .orThrow { it.exception }
            ?.let { Duration.ofSeconds(it.toLong()) }
            ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)

    fun getUnsuspendInterval(country: String, pmd: ProcurementMethod): Duration =
        ruleRepository.find(country, pmd, PARAMETER_UNSUSPEND_INTERVAL)
            .orThrow { it.exception }
            ?.let { Duration.ofSeconds(it.toLong()) }
            ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)

    fun getIntervalBefore(country: String, pmd: ProcurementMethod): Duration =
        ruleRepository.find(country, pmd, PARAMETER_INTERVAL_BEFORE)
            .orThrow { it.exception }
            ?.let { Duration.ofSeconds(it.toLong()) }
            ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)

    fun getRulesMinBids(country: String, pmd: ProcurementMethod): Int =
        ruleRepository.find(country, pmd, PARAMETER_MIN_BIDS)
            .orThrow { it.exception }
            ?.toInt()
            ?: throw ErrorException(ErrorType.BIDS_RULES_NOT_FOUND)

    fun getTenderPeriodMinimumDuration(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType
    ): Result<Duration, Fail> {
        val value = ruleRepository
            .find(country, pmd, MINIMUM_PERIOD_DURATION_PARAMETER, operationType)
            .onFailure { return it }
            ?: return ValidationError.EntityNotFound.TenderPeriodRule(
                country = country,
                pmd = pmd,
                parameter = MINIMUM_PERIOD_DURATION_PARAMETER,
                operationType = operationType
            ).asFailure()

        return value
            .tryToLong()
            .onFailure { incident ->
                return Fail.Incident.Database.Parsing(VALUE_COLUMN, value, incident.reason.exception)
                    .asFailure()
            }
            .let { Duration.ofSeconds(it) }
            .asSuccess()
    }

    fun getReturnInvitationsFlag(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType
    ): Result<Boolean, Fail> {
        val databaseQueryResult = ruleRepository
            .find(country, pmd, RETURN_INVITATIONS, operationType)

        val value = databaseQueryResult.onFailure { return it }
            .let { foundedValue ->
                foundedValue
                    ?: ruleRepository.find(country, pmd, RETURN_INVITATIONS)
                        .onFailure { return it }
                    ?: return ValidationError.EntityNotFound.ReturnInvitationsRule(
                        country = country,
                        pmd = pmd,
                        parameter = RETURN_INVITATIONS,
                        operationType = operationType
                    ).asFailure()
            }

        return value
            .tryToBoolean()
            .onFailure { incident ->
                return Fail.Incident.Database.Parsing(VALUE_COLUMN, value, incident.reason.exception)
                    .asFailure()
            }
            .asSuccess()
    }

    fun getValidStates(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType
    ): Result<BidStatesRule, Fail> {
        val states = ruleRepository.find(country, pmd, VALID_STATES_PARAMETER, operationType)
            .onFailure { fail -> return fail }
            ?: return ValidationError.EntityNotFound.ValidStatesRule(
                country = country,
                pmd = pmd,
                parameter = VALID_STATES_PARAMETER,
                operationType = operationType
            ).asFailure()

        return transform.tryDeserialization(states, BidStatesRule::class.java)
            .mapFailure { Fail.Incident.Database.Parsing(VALUE_COLUMN, states, it.exception) }
    }

    fun getExtensionAfterUnsuspended(country: String, pmd: ProcurementMethod): Duration {
        return ruleRepository.find(country, pmd, EXTENSION_AFTER_UNSUSPENDED)
            .orThrow { it.exception }
            ?.let { Duration.ofSeconds(it.toLong()) }
            ?: throw ErrorException(ErrorType.EXTENSION_AFTER_UNSUSPENDED_RULES_NOT_FOUND)
    }

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
        private const val PARAMETER_INTERVAL = "interval"
        private const val PARAMETER_UNSUSPEND_INTERVAL = "unsuspend_interval"
        private const val PARAMETER_INTERVAL_BEFORE = "interval_before"
        private const val MINIMUM_PERIOD_DURATION_PARAMETER = "minTenderPeriodDuration"
        private const val RETURN_INVITATIONS = "returnInvitations"
        private const val EXTENSION_AFTER_UNSUSPENDED = "extensionAfterUnsuspended"
        private const val VALID_STATES_PARAMETER = "validStates"

        private const val VALUE_COLUMN = "value"
    }
}
