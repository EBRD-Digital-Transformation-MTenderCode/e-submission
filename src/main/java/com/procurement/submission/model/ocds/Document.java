package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "documentType",
    "title",
    "description",
    "url",
    "datePublished",
    "dateModified",
    "format",
    "language",
    "relatedLots"
})
public class Document {
    @JsonProperty("id")
    @JsonPropertyDescription("A local, unique identifier for this document. This field is used to keep track of " +
        "multiple revisions of a document through the compilation from release to record mechanism.")
    @Size(min = 1)
    @NotNull
    private final String id;

    @JsonProperty("documentType")
    @JsonPropertyDescription("A classification of the document described taken from the [documentType codelist]" +
        "(http://standard.open-contracting.org/latest/en/schema/codelists/#document-type). Values from the provided " +
        "codelist should be used wherever possible, though extended values can be provided if the codelist does not " +
        "have a relevant code.")
    private final DocumentType documentType;

    @JsonProperty("title")
    @JsonPropertyDescription("The document title.")
    /*@Pattern(regexp = "^(title_(((([A-Za-z]{2,3}(-([A-Za-z]{3}(-[A-Za-z]{3}){0,2}))?)|[A-Za-z]{4}|[A-Za-z]{5,8})(-" +
        "([A-Za-z]{4}))?(-([A-Za-z]{2}|[0-9]{3}))?(-([A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3}))*(-([0-9A-WY-Za-wy-z]" +
        "(-[A-Za-z0-9]{2,8})+))*(-(x(-[A-Za-z0-9]{1,8})+))?)|(x(-[A-Za-z0-9]{1,8})+)))$")*/
    private final String title;

    @JsonProperty("description")
    @JsonPropertyDescription("A short description of the document. We recommend descriptions do not exceed 250 words." +
        " In the event the document is not accessible online, the description field can be used to describe " +
        "arrangements for obtaining a copy of the document.")
    /*@Pattern(regexp = "^(description_(((([A-Za-z]{2,3}(-([A-Za-z]{3}(-[A-Za-z]{3}){0,2}))?)|[A-Za-z]{4}|[A-Za-z]{5," +
        "8})(-([A-Za-z]{4}))?(-([A-Za-z]{2}|[0-9]{3}))?(-([A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3}))*(-([0-9A-WY-Za-wy-z]" +
        "(-[A-Za-z0-9]{2,8})+))*(-(x(-[A-Za-z0-9]{1,8})+))?)|(x(-[A-Za-z0-9]{1,8})+)))$")*/
    private final String description;

    @JsonProperty("url")
    @JsonPropertyDescription(" direct link to the document or attachment. The server providing access to this " +
        "document should be configured to correctly report the document mime type.")
    private final URI url;

    @JsonProperty("datePublished")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonPropertyDescription("The date on which the document was first published. This is particularly important for " +
        "legally important documents such as notices of a tender.")
    private final LocalDateTime datePublished;

    @JsonProperty("dateModified")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonPropertyDescription("Date that the document was last modified")
    private final LocalDateTime dateModified;

    @JsonProperty("format")
    @JsonPropertyDescription("The format of the document taken from the [IANA Media Types codelist](http://www.iana" +
        ".org/assignments/media-types/), with the addition of one extra value for 'offline/print', used when this " +
        "document entry is being used to describe the offline publication of a document. Use values from the template" +
        " column. Links to web pages should be tagged 'text/html'.")
    private final String format;

    @JsonProperty("language")
    @JsonPropertyDescription("Specifies the language of the linked document using either two-letter [ISO639-1]" +
        "(https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes), or extended [BCP47 language tags](http://www" +
        ".w3.org/International/articles/language-tags/). The use of lowercase two-letter codes from [ISO639-1]" +
        "(https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) is strongly recommended unless there is a clear user" +
        " need for distinguishing the language subtype.")
    private final String language;

    @JsonProperty("relatedLots")
    @JsonPropertyDescription("If this document relates to a particular lot, provide the identifier(s) of the related " +
        "lot(s) here.")
    private final List<String> relatedLots;

    @JsonCreator
    public Document(@JsonProperty("id") final String id,
                    @JsonProperty("documentType") final DocumentType documentType,
                    @JsonProperty("title") final String title,
                    @JsonProperty("description") final String description,
                    @JsonProperty("url") final URI url,
                    @JsonProperty("datePublished") @JsonDeserialize(using = LocalDateTimeDeserializer.class) final
                       LocalDateTime datePublished,
                    @JsonProperty("dateModified") @JsonDeserialize(using = LocalDateTimeDeserializer.class) final
                       LocalDateTime dateModified,
                    @JsonProperty("format") final String format,
                    @JsonProperty("language") final String language,
                    @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.id = id;
        this.documentType = documentType;
        this.title = title;
        this.description = description;
        this.url = url;
        this.datePublished = datePublished;
        this.dateModified = dateModified;
        this.format = format;
        this.language = language;
        this.relatedLots = relatedLots;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(documentType)
                                    .append(title)
                                    .append(description)
                                    .append(url)
                                    .append(datePublished)
                                    .append(dateModified)
                                    .append(format)
                                    .append(language)
                                    .append(relatedLots)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Document)) {
            return false;
        }
        final Document rhs = (Document) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(documentType, rhs.documentType)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(url, rhs.url)
                                  .append(datePublished, rhs.datePublished)
                                  .append(dateModified, rhs.dateModified)
                                  .append(format, rhs.format)
                                  .append(language, rhs.language)
                                  .append(relatedLots, rhs.relatedLots)
                                  .isEquals();
    }

    public enum DocumentType {
        TENDER_NOTICE("tenderNotice"),
        AWARD_NOTICE("awardNotice"),
        CONTRACT_NOTICE("contractNotice"),
        COMPLETION_CERTIFICATE("completionCertificate"),
        PROCUREMENT_PLAN("procurementPlan"),
        BIDDING_DOCUMENTS("biddingDocuments"),
        TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
        EVALUATION_CRITERIA("evaluationCriteria"),
        EVALUATION_REPORTS("evaluationReports"),
        CONTRACT_DRAFT("contractDraft"),
        CONTRACT_SIGNED("contractSigned"),
        CONTRACT_ARRANGEMENTS("contractArrangements"),
        CONTRACT_SCHEDULE("contractSchedule"),
        PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
        FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
        FINAL_AUDIT("finalAudit"),
        HEARING_NOTICE("hearingNotice"),
        MARKET_STUDIES("marketStudies"),
        ELIGIBILITY_CRITERIA("eligibilityCriteria"),
        CLARIFICATIONS("clarifications"),
        SHORTLISTED_FIRMS("shortlistedFirms"),
        ENVIRONMENTAL_IMPACT("environmentalImpact"),
        ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
        RISK_PROVISIONS("riskProvisions"),
        WINNING_BID("winningBid"),
        COMPLAINTS("complaints"),
        CONTRACT_ANNEXE("contractAnnexe"),
        CONTRACT_GUARANTEES("contractGuarantees"),
        SUB_CONTRACT("subContract"),
        NEEDS_ASSESSMENT("needsAssessment"),
        FEASIBILITY_STUDY("feasibilityStudy"),
        PROJECT_PLAN("projectPlan"),
        BILL_OF_QUANTITY("billOfQuantity"),
        BIDDERS("bidders"),
        CONFLICT_OF_INTEREST("conflictOfInterest"),
        DEBARMENTS("debarments"),
        ILLUSTRATION("illustration"),
        SUBMISSION_DOCUMENTS("submissionDocuments"),
        CONTRACT_SUMMARY("contractSummary"),
        CANCELLATION_DETAILS("cancellationDetails");

        private static final Map<String, DocumentType> CONSTANTS = new HashMap<>();
        private final String value;

        static {
            for (final DocumentType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        DocumentType(final String value) {
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
        public static DocumentType fromValue(final String value) {
            final DocumentType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }
    }
}
