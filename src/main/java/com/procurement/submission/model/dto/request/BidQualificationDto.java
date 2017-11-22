package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "date",
    "status",
    "tenderers",
    "documents",
    "relatedLots"
})
public class BidQualificationDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @NotNull
    @JsonProperty("status")
    private BidStatus status;

    @NotEmpty
    @JsonProperty("tenderers")
    private List<OrganizationReferenceDto> tenderers;

    @NotEmpty
    @JsonProperty("documents")
    private List<DocumentDto> documents;

    @JsonProperty("relatedLots")
    private final List<String> relatedLots;

    @JsonCreator
    public BidQualificationDto(
        @JsonProperty("id") final String id,
        @JsonProperty("date") final LocalDateTime date,
        @JsonProperty("status") final BidStatus status,
        @JsonProperty("tenderers") final List<OrganizationReferenceDto> tenderers,
        @JsonProperty("documents") final List<DocumentDto> documents,
        @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.tenderers = tenderers;
        this.documents = documents;
        this.relatedLots = relatedLots;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(date)
                                    .append(status)
                                    .append(tenderers)
                                    .append(documents)
                                    .append(relatedLots)
                                    .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BidQualificationDto)) {
            return false;
        }
        final BidQualificationDto bqd = (BidQualificationDto) obj;
        return new EqualsBuilder().append(id, bqd.id)
                                  .append(date, bqd.date)
                                  .append(status, bqd.status)
                                  .append(tenderers, bqd.tenderers)
                                  .append(documents, bqd.documents)
                                  .append(relatedLots, bqd.relatedLots)
                                  .isEquals();
    }
}
