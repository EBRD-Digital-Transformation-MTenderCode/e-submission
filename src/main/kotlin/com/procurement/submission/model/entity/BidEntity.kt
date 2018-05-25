package com.procurement.submission.model.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

//@Table("submission_bid")
data class BidEntity(

//        @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
        val cpId: String,

//        @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
//        @PrimaryKeyColumn(name = "bid_id", type = PrimaryKeyType.CLUSTERED)
        val bidId: UUID,

//        @PrimaryKeyColumn(name = "token_entity", type = PrimaryKeyType.CLUSTERED)
        val token: UUID,

        val stage: String,

//        @Column("owner")
        val owner: String,

//        @Column("status")
        val status: String,

//        @Column("created_date")
        var createdDate: Date,

//        @Column("json_data")
        val jsonData: String
)
