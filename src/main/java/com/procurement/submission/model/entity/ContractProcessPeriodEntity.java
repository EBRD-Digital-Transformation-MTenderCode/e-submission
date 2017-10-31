package com.procurement.submission.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contract_period")
@Getter
@Setter
public class ContractProcessPeriodEntity {
    @PrimaryKeyColumn(name = "tender_id", type = PrimaryKeyType.PARTITIONED)
    private String tenderId;

    @Column(value = "start_date")
    private LocalDateTime startDate;

    @Column(value = "end_date")
    private LocalDateTime endDate;
}
