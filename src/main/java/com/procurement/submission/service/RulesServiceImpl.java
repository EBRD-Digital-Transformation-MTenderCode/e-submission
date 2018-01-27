package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {
    private static final String PARAMETER_MINBIDS = "minBids";
    private static final String PARAMETER_INTERVAL = "interval";

    private RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public int getInterval(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_INTERVAL))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException("We don't have rules with country: " + country +
                        ", method: " + method + ", parameter: " + PARAMETER_INTERVAL));
    }

    @Override
    public int getRulesMinBids(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_MINBIDS))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException("We don't have rules with country: " + country +
                        ", method: " + method + ", parameter: " + PARAMETER_MINBIDS));
    }
}
