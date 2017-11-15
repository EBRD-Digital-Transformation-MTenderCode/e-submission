
package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "email",
    "telephone",
    "faxNumber",
    "url",
    "languages"
})
public class ContactPointDto {
    @JsonProperty("name")
    @JsonPropertyDescription("The name of the contact person, department, or contact point, for correspondence " +
        "relating to this contracting process.")
    @Pattern(regexp = "^(name_(((([A-Za-z]{2,3}(-([A-Za-z]{3}(-[A-Za-z]{3}){0,2}))?)|[A-Za-z]{4}|[A-Za-z]{5,8})(-" +
        "([A-Za-z]{4}))?(-([A-Za-z]{2}|[0-9]{3}))?(-([A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3}))*(-([0-9A-WY-Za-wy-z]" +
        "(-[A-Za-z0-9]{2,8})+))*(-(x(-[A-Za-z0-9]{1,8})+))?)|(x(-[A-Za-z0-9]{1,8})+)))$")
    private final String name;

    @Email
    @JsonProperty("email")
    @JsonPropertyDescription("The e-mail address of the contact point/person.")
    private final String email;

    @JsonProperty("telephone")
    @JsonPropertyDescription("The telephone number of the contact point/person. This should include the international" +
        " dialing code.")
    private final String telephone;

    @JsonProperty("faxNumber")
    @JsonPropertyDescription("The fax number of the contact point/person. This should include the international " +
        "dialing code.")
    private final String faxNumber;

    @JsonProperty("url")
    @JsonPropertyDescription("A web address for the contact point/person.")
    private final URI url;

    @JsonProperty("languages")
    private List<String> languages;

    @JsonCreator
    public ContactPointDto(@JsonProperty("name") final String name,
                           @JsonProperty("email") final String email,
                           @JsonProperty("telephone") final String telephone,
                           @JsonProperty("faxNumber") final String faxNumber,
                           @JsonProperty("url") final URI url,
                           @JsonProperty("languages") final List<String> languages) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.faxNumber = faxNumber;
        this.url = url;
        this.languages = languages;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name)
                                    .append(email)
                                    .append(telephone)
                                    .append(faxNumber)
                                    .append(url)
                                    .append(languages)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContactPointDto)) {
            return false;
        }
        final ContactPointDto rhs = (ContactPointDto) other;
        return new EqualsBuilder().append(name, rhs.name)
                                  .append(email, rhs.email)
                                  .append(telephone, rhs.telephone)
                                  .append(faxNumber, rhs.faxNumber)
                                  .append(url, rhs.url)
                                  .append(languages, rhs.languages)
                                  .isEquals();
    }
}
