package com.procurement.submission.exception;

public enum ErrorType {

    BID_NOT_FOUND("00.01", "Bids not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_ID("00.03", "Invalid bid id."),
    PERIOD_NOT_FOUND("00.01", "Period not found."),
    INVALID_DATE("00.03", "Date does not match the period."),
    INVALID_PERIOD("00.03", "Invalid period."),
    PERIOD_NOT_EXPIRED("00.03", "Period has not yet expired."),
    INTERVAL_RULES_NOT_FOUND("00.03", "Interval rules not found."),
    BIDS_RULES_NOT_FOUND("00.03", "Bids rules not found."),
    BID_ALREADY_WITH_LOT("00.04", "We already have Bid with this Lots and Tenderers.");

    private final String code;
    private final String message;

    ErrorType(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
