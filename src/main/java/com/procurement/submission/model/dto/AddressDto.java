package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String countryName;
}