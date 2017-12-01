package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {
    private static final String PARAMETER_MINBIDS = "minBids";

    private RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public long getInterval(final PeriodDataDto data) {
        return getValue(data, "interval").map(Long::valueOf)
                                         .orElse(0L);
    }

    // TODO: 24.11.17 Create TEST
    @Override
    public int getRulesMinBids(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_MINBIDS))
                       .map(Integer::parseInt)
                       .orElseThrow(() -> new ErrorException("We don't have rules with country: " + country +
                           ", method: " + method + ", parameter: " + PARAMETER_MINBIDS));
    }

    public Optional<String> getValue(final PeriodDataDto data, final String parameter) {
        final String value = rulesRepository.getValue(data.getCountry(),
                                                data.getProcurementMethodDetails(),
                                                parameter);
        return Optional.of(value);
    }
}
