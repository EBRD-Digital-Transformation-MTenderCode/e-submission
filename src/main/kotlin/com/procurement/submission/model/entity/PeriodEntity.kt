package com.procurement.submission.model.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table("submission_period")
data class PeriodEntity(

        @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
        val cpId: String,

        @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
        val stage: String,

        @Column("start_date")
        var startDate: Date,

        @Column("end_date")
        var endDate: Date
)
