package com.axolotl.jobmatcher.dto.company;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompanyResponse {
    private UUID id;
    private String name;
    private String description;
    private String website;
    private LocalDateTime createdAt;
    private String contactEmail;
}