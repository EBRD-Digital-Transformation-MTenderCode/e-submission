package com.procurement.submission.service;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

    ResponseDto checkPeriod(PeriodDataDto data);

    void checkPeriod(String ocid);

    ResponseDto savePeriod(PeriodDataDto data);
}
