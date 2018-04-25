package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.submission.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum StatusDetails {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty");

    private static final Map<String, StatusDetails> CONSTANTS = new HashMap<String, StatusDetails>();
    private final String value;

    static {
        for (final StatusDetails c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    StatusDetails(final String value) {
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
    public static StatusDetails fromValue(final String value) {
        final StatusDetails constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(StatusDetails.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }
}
