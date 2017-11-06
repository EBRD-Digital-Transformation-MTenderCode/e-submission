package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.SubmissionPeriodDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.SubmissionPeriodRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SubmissionPeriodServiceImpl implements SubmissionPeriodService {

    private SubmissionPeriodRepository submissionPeriodRepository;

    public SubmissionPeriodServiceImpl(SubmissionPeriodRepository submissionPeriodRepository) {
        this.submissionPeriodRepository = submissionPeriodRepository;
    }

    @Override
    public void insertData(SubmissionPeriodDto dataDto) {
        Objects.requireNonNull(dataDto);
        convertDtoToEntity(dataDto)
            .ifPresent(period -> submissionPeriodRepository.save(period));
    }

    public Optional<SubmissionPeriodEntity> convertDtoToEntity(SubmissionPeriodDto dataDto) {
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId(dataDto.getOcId());
        submissionPeriodEntity.setStartDate(dataDto.getStartDate());
        submissionPeriodEntity.setEndDate(dataDto.getEndDate());
        return Optional.of(submissionPeriodEntity);
    }
}
