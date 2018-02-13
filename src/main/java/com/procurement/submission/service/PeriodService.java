package com.procurement.submission.service;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.entity.PeriodEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

    ResponseDto savePeriod(String cpId,
                           String stage,
                           LocalDateTime startDate,
                           LocalDateTime endDate);

    ResponseDto saveNewPeriod(String cpId, String stage, String country, String pmd, LocalDateTime startDate);

    void checkCurrentDateInPeriod(String cpId, String stage);

    void checkIsPeriodExpired(String cpId, String stage);

    PeriodEntity getPeriod(String cpId, String stage);

    ResponseDto checkPeriod(String cpId,
                            String country,
                            String pmd,
                            String stage,
                            LocalDateTime startDate,
                            LocalDateTime endDate);

    ResponseDto periodValidation(String country,
                                 String pmd,
                                 LocalDateTime startDate,
                                 LocalDateTime endDate);

}
