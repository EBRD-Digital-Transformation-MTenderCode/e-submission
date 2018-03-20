package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AwardStatusDetails {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty");

    private static final Map<String, AwardStatusDetails> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final AwardStatusDetails c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    AwardStatusDetails(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AwardStatusDetails fromValue(final String value) {
        final AwardStatusDetails constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(
                    "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

}
