package com.procurement.submission.service

import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.infrastructure.dao.RulesDao
import org.springframework.stereotype.Service

@Service
class RulesService(private val rulesDao: RulesDao) {

    fun getInterval(country: String, pmd: String): Long {
        return rulesDao.getValue(country, pmd, PARAMETER_INTERVAL)?.toLongOrNull()
                ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)
    }

    fun getUnsuspendInterval(country: String, pmd: String): Long {
        return rulesDao.getValue(country, pmd, PARAMETER_UNSUSPEND_INTERVAL)?.toLongOrNull()
                ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)
    }

    fun getIntervalBefore(country: String, pmd: String): Long {
        return rulesDao.getValue(country, pmd, PARAMETER_INTERVAL_BEFORE)?.toLongOrNull()
                ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)
    }


    fun getRulesMinBids(country: String, pmd: String): Int {
        return rulesDao.getValue(country, pmd, PARAMETER_MIN_BIDS)?.toIntOrNull()
                ?: throw ErrorException(ErrorType.BIDS_RULES_NOT_FOUND)
    }

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
        private const val PARAMETER_INTERVAL = "interval"
        private const val PARAMETER_UNSUSPEND_INTERVAL = "unsuspend_interval"
        private const val PARAMETER_INTERVAL_BEFORE = "interval_before"
    }
}
