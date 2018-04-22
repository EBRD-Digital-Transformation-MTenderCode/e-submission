package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.exception.ErrorType;
import com.procurement.submission.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {

    private static final String PARAMETER_MIN_BIDS = "minBids";
    private static final String PARAMETER_INTERVAL = "interval";
    private static final String PARAMETER_UNSUSPEND_INTERVAL = "unsuspend_interval";

    private RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public int getInterval(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_INTERVAL))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND));
    }

    @Override
    public int getUnsuspendInterval(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_UNSUSPEND_INTERVAL))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException(ErrorType.INTERVAL_RULES_NOT_FOUND));
    }

    @Override
    public int getRulesMinBids(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_MIN_BIDS))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException(ErrorType.BIDS_RULES_NOT_FOUND));
    }
}
