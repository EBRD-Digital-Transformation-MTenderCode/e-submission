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

@Getter
@Setter
@Table("submission_bid")
public class BidEntity {
    @PrimaryKeyColumn(name = "oc_id", type = PrimaryKeyType.PARTITIONED)
    private String ocId;

    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
    private String stage;

    @PrimaryKeyColumn(name = "bid_id", type = PrimaryKeyType.CLUSTERED)
    private String bidId;

    @PrimaryKeyColumn(name = "token_entity", type = PrimaryKeyType.CLUSTERED)
    private String token;

    @Column("owner")
    private String owner;

    @Column(value = "status")
    private String status;

    @Column("created_date")
    private Date createdDate;

    @Column("pending_date")
    private Date pendingDate;

    @Column(value = "json_data")
    private String jsonData;

    public LocalDateTime getCreatedDate() {
        return LocalDateTime.ofInstant(createdDate.toInstant(), ZoneOffset.UTC);
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = Date.from(createdDate.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getPendingDate() {
        return LocalDateTime.ofInstant(pendingDate.toInstant(), ZoneOffset.UTC);
    }

    public void setPendingDate(LocalDateTime pendingDate) {
        this.pendingDate = Date.from(pendingDate.toInstant(ZoneOffset.UTC));
    }
}
