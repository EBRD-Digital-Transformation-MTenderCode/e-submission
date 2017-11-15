package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {

    private RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public long getInterval(final PeriodDataDto data) {
        return getValue(data, "interval").map(Long::valueOf)
                                         .orElse(0L);
    }

    public Optional<String> getValue(final PeriodDataDto data, final String parameter) {
        final String value = rulesRepository.getValue(data.getCountry(),
                                                data.getProcurementMethodDetails(),
                                                parameter);
        return Optional.of(value);
    }
}
