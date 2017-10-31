package com.procurement.submission.model.dto.response;

import com.procurement.submission.model.dto.BidDto;
import com.procurement.submission.model.dto.PeriodDto;
import com.procurement.submission.model.dto.RequirementReferenceDto;
import java.util.List;
import lombok.Data;
import lombok.Getter;

@Data
public class RequirementResponseDto {
    private String id;
    private String title;
    private String description;
    private Object value;
    private PeriodDto period;
    private RequirementReferenceDto requirement;
    private OrganizationReferenceDto relatedTenderer;

    @Getter
    public static class OrganizationReferenceDto {
        private String name;
        private Integer id;
        private IdentifierDto identifier;
        private AddressDto address;
        private List<IdentifierDto> additionalIdentifiers;
        private ContactPointDto contactPoint;

        public class IdentifierDto {
            private String scheme;
            private String id;
            private String legalName;
            private String uri;
        }

        public class AddressDto {
            private String streetAddress;
            private String locality;
            private String region;
            private String postalCode;
            private String countryName;
        }

        public class ContactPointDto {
            private String name;
            private String email;
            private String telephone;
            private String faxNumber;
            private String url;
            private List<String> languages;
        }
    }
}
