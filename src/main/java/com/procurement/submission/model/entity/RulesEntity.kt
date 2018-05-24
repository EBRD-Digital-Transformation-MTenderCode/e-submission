package com.procurement.submission.model.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("submission_rules")
data class RulesEntity(

        @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
        val country: String,

        @PrimaryKeyColumn(name = "pmd", type = PrimaryKeyType.CLUSTERED)
        val method: String,

        @PrimaryKeyColumn(name = "parameter", type = PrimaryKeyType.CLUSTERED)
        val parameter: String,

        @Column("value")
        val value: String
)


