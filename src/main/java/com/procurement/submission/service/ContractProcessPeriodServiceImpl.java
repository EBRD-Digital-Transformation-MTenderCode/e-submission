package com.procurement.submission.service;

import com.procurement.submission.model.dto.ContractProcessPeriodDto;
import com.procurement.submission.model.entity.ContractProcessPeriodEntity;
import com.procurement.submission.repository.ContractProcessPeriodRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ContractProcessPeriodServiceImpl implements ContractProcessPeriodService {

    private ContractProcessPeriodRepository contractProcessPeriodRepository;

    public ContractProcessPeriodServiceImpl(ContractProcessPeriodRepository tenderPeriodRepository) {
        this.contractProcessPeriodRepository = tenderPeriodRepository;
    }

    @Override
    public void insertData(ContractProcessPeriodDto dataDto) {
        Objects.requireNonNull(dataDto);
        convertDtoToEntity(dataDto)
            .ifPresent(period -> contractProcessPeriodRepository.save(period));
    }

    public Optional<ContractProcessPeriodEntity> convertDtoToEntity(ContractProcessPeriodDto dataDto) {
        ContractProcessPeriodEntity contractProcessPeriodEntity = new ContractProcessPeriodEntity();
        contractProcessPeriodEntity.setTenderId(dataDto.getTenderId());
        contractProcessPeriodEntity.setStartDate(dataDto.getStartDate());
        contractProcessPeriodEntity.setEndDate(dataDto.getEndDate());
        return Optional.of(contractProcessPeriodEntity);
    }
}
