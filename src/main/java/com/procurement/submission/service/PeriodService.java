package com.procurement.submission.service;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.entity.PeriodEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

    ResponseDto checkInterval(String country,
                            String pmd,
                            String stage,
                            LocalDateTime startDate,
                            LocalDateTime endDate);

    ResponseDto savePeriod(String cpid,
                           String stage,
                           LocalDateTime startDate,
                           LocalDateTime endDate);

    void checkCurrentDateInPeriod(String cpid);

    void checkIsPeriodExpired(String cpId);

    PeriodEntity getPeriod(String cpId);

}
