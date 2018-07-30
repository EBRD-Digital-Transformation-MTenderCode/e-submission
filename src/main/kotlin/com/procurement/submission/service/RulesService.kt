package com.procurement.submission.service

import com.procurement.submission.dao.RulesDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import org.springframework.stereotype.Service

interface RulesService {

    fun getInterval(country: String, method: String): Long

    fun getUnsuspendInterval(country: String, method: String): Long

    fun getRulesMinBids(country: String, method: String): Int
}

@Service
class RulesServiceImpl(private val rulesDao: RulesDao) : RulesService {

    override fun getInterval(country: String, method: String): Long {
        return rulesDao.getValue(country, method, PARAMETER_INTERVAL)?.toLongOrNull()
                ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)
    }

    override fun getUnsuspendInterval(country: String, method: String): Long {
        return rulesDao.getValue(country, method, PARAMETER_UNSUSPEND_INTERVAL)?.toLongOrNull()
                ?: throw ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND)
    }

    override fun getRulesMinBids(country: String, method: String): Int {
        return rulesDao.getValue(country, method, PARAMETER_MIN_BIDS)?.toIntOrNull()
                ?: throw ErrorException(ErrorType.BIDS_RULES_NOT_FOUND)
    }

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
        private const val PARAMETER_INTERVAL = "interval"
        private const val PARAMETER_UNSUSPEND_INTERVAL = "unsuspend_interval"
    }
}
