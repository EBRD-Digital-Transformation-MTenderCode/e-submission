package com.procurement.submission.service;

import com.procurement.submission.model.dto.ContractProcessPeriodDto;
import org.springframework.stereotype.Service;

@Service
public interface ContractProcessPeriodService {

    void insertData(ContractProcessPeriodDto data);
}
