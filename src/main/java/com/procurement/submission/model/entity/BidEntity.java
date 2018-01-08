package com.procurement.submission.model.entity;

import com.procurement.submission.model.ocds.Bid;
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

    @PrimaryKeyColumn(name = "bid_token", type = PrimaryKeyType.CLUSTERED)
    private UUID bidToken;

    @Column(value = "json_data")
    private String jsonData;

    @Column(value = "bid_status")
    private Bid.Status status;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("pending_date")
    private LocalDateTime pendingDate;

    @Column("owner")
    private String owner;
}
