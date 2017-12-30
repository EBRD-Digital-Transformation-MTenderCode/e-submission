package com.procurement.submission.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("submission_period")
@Getter
@Setter
public class SubmissionPeriodEntity {
    @PrimaryKeyColumn(name = "oc_id", type = PrimaryKeyType.PARTITIONED)
    private String ocId;

    @Column(value = "start_date")
    private Date startDate;

    @Column(value = "end_date")
    private Date endDate;

    public LocalDateTime getStartDate() {
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneOffset.UTC);
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.ofInstant(endDate.toInstant(), ZoneOffset.UTC);
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = Date.from(endDate.toInstant(ZoneOffset.UTC));
    }
}
