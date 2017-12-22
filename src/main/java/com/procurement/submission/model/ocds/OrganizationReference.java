package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "identifier",
    "address",
    "additionalIdentifiers",
    "contactPoint"
})
public class OrganizationReference {
    @JsonProperty("id")
    @JsonPropertyDescription("The id of the party being referenced. This must match the id of an entry in the parties" +
        " section.")
    private final String id;

    @JsonProperty("name")
    @JsonPropertyDescription("The name of the party being referenced. This must match the name of an entry in the " +
        "parties section.")
    @Size(min = 1)
    @NotNull
    private final String name;

    @JsonProperty("identifier")
    @Valid
    @NotNull
    private final Identifier identifier;

    @JsonProperty("additionalIdentifiers")
    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonPropertyDescription("(Deprecated outside the parties section) A list of additional / supplemental " +
        "identifiers for the organization, using the [organization identifier guidance](http://standard" +
        ".open-contracting.org/latest/en/schema/identifiers/). This could be used to provide an internally used " +
        "identifier for this organization in addition to the primary legal entity identifier.")
    @Valid
    private final Set<Identifier> additionalIdentifiers;

    @JsonProperty("address")
    @Valid
    private final Address address;

    @JsonProperty("contactPoint")
    @Valid
    private final ContactPoint contactPoint;

    @JsonCreator
    public OrganizationReference(@JsonProperty("id") final String id,
                                 @JsonProperty("name") final String name,
                                 @JsonProperty("identifier") final Identifier identifier,
                                 @JsonProperty("address") final Address address,
                                 @JsonProperty("additionalIdentifiers")
                                     final LinkedHashSet<Identifier> additionalIdentifiers,
                                 @JsonProperty("contactPoint") final ContactPoint contactPoint) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.address = address;
        this.additionalIdentifiers = additionalIdentifiers;
        this.contactPoint = contactPoint;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(name)
                                    .append(identifier)
                                    .append(address)
                                    .append(additionalIdentifiers)
                                    .append(contactPoint)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof OrganizationReference)) {
            return false;
        }
        final OrganizationReference rhs = (OrganizationReference) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(name, rhs.name)
                                  .append(identifier, rhs.identifier)
                                  .append(address, rhs.address)
                                  .append(additionalIdentifiers, rhs.additionalIdentifiers)
                                  .append(contactPoint, rhs.contactPoint)
                                  .isEquals();
    }
}
