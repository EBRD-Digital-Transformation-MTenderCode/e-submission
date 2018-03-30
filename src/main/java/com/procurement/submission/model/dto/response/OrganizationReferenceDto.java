package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrganizationReferenceDto {

    private String id;

    private String name;

    @JsonCreator
    public OrganizationReferenceDto(@JsonProperty("id") @NotNull final String id,
                                    @JsonProperty("name") @NotNull final String name) {
        this.id = id;
        this.name = name;
    }
}

