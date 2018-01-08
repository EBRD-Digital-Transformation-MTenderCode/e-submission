package com.procurement.submission.service;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface PeriodService {

    ResponseDto checkPeriod(String country,
                            String pmd,
                            String stage,
                            LocalDateTime startDate,
                            LocalDateTime endDate);

    ResponseDto savePeriod(String cpid,
                           String stage,
                           LocalDateTime startDate,
                           LocalDateTime endDate);

    void checkPeriod(String ocid);
}
