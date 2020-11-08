package com.procurement.submission.infrastructure.repository

object Database {
    const val KEYSPACE = "submission"

    object History {
        const val TABLE = "history"
        const val COMMAND_ID = "command_id"
        const val COMMAND_NAME = "command_name"
        const val COMMAND_DATE = "command_date"
        const val JSON_DATA = "json_data"
    }

    object Bids {
        const val TABLE = "bids"
        const val CPID = "cpid"
        const val OCID = "ocid"
        const val ID = "id"
        const val OWNER = "owner"
        const val TOKEN = "token_entity"
        const val STATUS = "status"
        const val CREATED_DATE = "created_date"
        const val PENDING_DATE = "pending_date"
        const val JSON_DATA = "json_data"
    }

    object Period {
        const val TABLE = "periods"
        const val CPID = "cpid"
        const val OCID = "ocid"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }

    object Rules {
        const val TABLE = "rules"
        const val COUNTRY = "country"
        const val PMD = "pmd"
        const val OPERATION_TYPE = "operation_type"
        const val PARAMETER = "parameter"
        const val VALUE = "value"
    }
}
