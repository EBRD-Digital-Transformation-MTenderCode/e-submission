package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorInsertException;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.QualificationOfferResponseDto;
import com.procurement.submission.model.entity.ContractProcessPeriodEntity;
import com.procurement.submission.repository.ContractProcessPeriodRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {
    private ContractProcessPeriodRepository periodRepository;

    @Override
    public QualificationOfferResponseDto insertQualificationOffer(final QualificationOfferDto qualificationOfferDto) {
        final LocalDateTime localDateTime = LocalDateTime.now();
        checkPeriod(localDateTime, qualificationOfferDto.getTenderId());



        return null;
    }

    private void checkPeriod(final LocalDateTime localDateTime, final String tenderId) {
        final ContractProcessPeriodEntity periodEntity = periodRepository.getByTenderId(tenderId);
        boolean localDateTimeAfter = localDateTime.isAfter(periodEntity.getStartDate());
        boolean localDateTimeBefore = localDateTime.isBefore(periodEntity.getEndDate());
        if (!localDateTimeAfter && !localDateTimeBefore) {
            throw new ErrorInsertException("Not found date");
        }
    }
}
