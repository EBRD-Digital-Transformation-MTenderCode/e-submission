package com.procurement.submission.model.entity;

import com.procurement.submission.model.ocds.BidStatus;
import java.time.LocalDateTime;
import java.util.UUID;
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
    private UUID bidId;

    @Column(value = "json_data")
    private String jsonData;

    @Column(value = "bid_status")
    private BidStatus status;

    // TODO: 22.12.17
    @Column("createdDate")
    private LocalDateTime createdDate;

    @Column("pendingDate")
    private LocalDateTime pendingDate;
}
