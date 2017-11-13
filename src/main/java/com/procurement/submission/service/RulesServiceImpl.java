package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {

    private RulesRepository rulesRepository;

    public RulesServiceImpl(RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public Long getInterval(PeriodDataDto data) {
        return getValue(data, "interval").map(r -> Long.valueOf(r))
                                         .orElse(0L);
    }

    public Optional<String> getValue(PeriodDataDto data, String parameter) {
        String value = rulesRepository.getValue(data.getCountry(),
                                                data.getProcurementMethodDetails(),
                                                parameter);
        return Optional.of(value);
    }
}
