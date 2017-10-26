package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class ContactPointDto {
    private String name;
    private String email;
    private String telephone;
    private String faxNumber;
    private String url;
    private List<String> languages;
}