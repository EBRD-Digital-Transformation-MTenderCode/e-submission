package com.procurement.submission.application.params

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import com.procurement.submission.domain.model.enums.DocumentType
import com.procurement.submission.domain.model.enums.PersonTitle
import com.procurement.submission.model.dto.ocds.PersonId
import java.time.LocalDateTime

data class PersonesProcessingParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val parties: List<Party>
) {
    data class Party(
        val id: String,
        val persones: List<Persone>
    ) {
        data class Persone(
            val id: PersonId,
            val title: PersonTitle,
            val name: String,
            val identifier: Identifier,
            val businessFunctions: List<BusinessFunction>
        ) {
            data class Identifier(
                val scheme: String,
                val id: String,
                val uri: String?
            )

            data class BusinessFunction(
                val id: String,
                val type: BusinessFunctionType,
                val jobTitle: String,
                val period: Period,
                val documents: List<Document>?
            ) {
                data class Period(
                    val startDate: LocalDateTime
                )

                data class Document(
                    val id: String,
                    val documentType: BusinessFunctionDocumentType,
                    val title: String,
                    val description: String?
                )
            }
        }
    }
}