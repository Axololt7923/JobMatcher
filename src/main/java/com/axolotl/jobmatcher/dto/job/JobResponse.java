package com.axolotl.jobmatcher.dto.job;

import com.axolotl.jobmatcher.entity.Job;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobResponse {
    private UUID id;
    private String title;
    private String description;
    private String requirements;
    private Integer salaryMin;
    private Integer salaryMax;
    private String location;
    private Job.JobType jobType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private UUID companyId;
    private String companyName;
    private UUID createdBy;

    @NotBlank
    private String sourceUrl;
    private String contactEmail;

}