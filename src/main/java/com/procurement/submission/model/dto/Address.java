package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class Address {
    public String streetAddress;
    public String locality;
    public String region;
    public String postalCode;
    public String countryName;
}