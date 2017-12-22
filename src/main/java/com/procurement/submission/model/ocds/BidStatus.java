package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum BidStatus {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn");

    private static final Map<String, BidStatus> CONSTANTS = new HashMap<String, BidStatus>();
    private final String value;

    static {
        for (final BidStatus c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    BidStatus(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static BidStatus fromValue(final String value) {
        final BidStatus constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        }
        return constant;
    }
}
