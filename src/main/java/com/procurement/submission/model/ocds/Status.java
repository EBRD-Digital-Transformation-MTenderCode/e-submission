package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.submission.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Status {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty");

    private static final Map<String, Status> CONSTANTS = new HashMap<String, Status>();
    private final String value;

    static {
        for (final Status c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    Status(final String value) {
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
    public static Status fromValue(final String value) {
        final Status constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(Status.class.getName(), value, Arrays.toString(values()));
        }
        return constant;
    }
}
