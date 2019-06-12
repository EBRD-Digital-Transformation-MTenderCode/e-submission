package com.procurement.submission.exception

enum class ErrorType constructor(val code: String, val message: String) {
    INVALID_JSON_TYPE("00.00", "Invalid type: "),
    BID_NOT_FOUND("00.01", "Bids not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_TOKEN("00.03", "Invalid token."),
    INVALID_ID("00.04", "Bid id must not be empty."),
    INVALID_RELATED_LOT("00.05", "Invalid related lots in documents."),
    ID_NOT_NULL("00.06", "Bid id must be empty."),
    STATUS_IS_NULL("00.07", "Status must not be empty."),
    STATUS_DETAIL_IS_NULL("00.08", "Status detail must not be empty."),
    VALUE_IS_NULL("00.09", "Value of bid must not be empty."),
    INVALID_DTO("00.10", "Invalid Dto"),
    TENDERERS_IS_EMPTY("00.11", "The list of the tenderers is empty."),
    PERIOD_NOT_FOUND("01.01", "Period not found."),
    INVALID_PERIOD("01.02", "Invalid period."),
    INVALID_DATE("01.03", "Date does not match the period."),
    PERIOD_NOT_EXPIRED("01.04", "Period has not yet expired."),
    INTERVAL_RULES_NOT_FOUND("02.01", "Interval rules not found."),
    BIDS_RULES_NOT_FOUND("02.02", "Bids rules not found."),
    BID_ALREADY_WITH_LOT("03.01", "We already have Bid with this Lots and Tenderers."),
    RELATED_LOTS_MUST_BE_ONE_UNIT("03.02", "Related lots must be one unit"),
    CREATE_BID_DOCUMENTS_SUBMISSION("03.03", "Documents must be contains one 'submissionDocuments' type"),
    CREATE_BID_DOCUMENTS_TYPES("03.04", "Invalid types of documents"),
    INVALID_STATUSES_FOR_UPDATE("03.05", "Invalid status or status details"),
    INVALID_DOCS_ID("03.06", "Invalid documents ids."),
    INVALID_DOCS_FOR_UPDATE("03.07", "Invalid documents for update."),
    AWARD_CRITERIA("03.08", "Award Criteria can't be recognized"),
    CONTEXT("20.01", "Context parameter not found."),
    EMPTY_RELATED_LOTS("20.02","Related lots must be presented!");

}
