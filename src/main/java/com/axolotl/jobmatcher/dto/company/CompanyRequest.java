package com.axolotl.jobmatcher.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank
    private String name;
    private String contactEmail;

    private String description;
    private String website;

}