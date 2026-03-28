package com.axolotl.jobmatcher.dto.cv;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CVResponse {
    private UUID id;
    private UUID userId;
    private String fileUrl;
    private String chromaId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String[] skills;
    private Float experienceYears;
    private String educationLevel;
    private String[] languages;
    private String summary;
}