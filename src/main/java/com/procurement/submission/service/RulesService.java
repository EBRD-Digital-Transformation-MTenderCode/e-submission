package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import org.springframework.stereotype.Service;

@Service
public interface RulesService {

    long getInterval(PeriodDataDto data);
}
