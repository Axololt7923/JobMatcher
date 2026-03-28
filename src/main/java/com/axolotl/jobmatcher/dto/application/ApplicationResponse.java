package com.axolotl.jobmatcher.dto.application;

import com.axolotl.jobmatcher.entity.Application;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Deprecated(forRemoval = false)
@Data
@Builder
public class ApplicationResponse {
    private UUID id;
    private UUID userId;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private UUID cvId;
    private Application.Status status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}