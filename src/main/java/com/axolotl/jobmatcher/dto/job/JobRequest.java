package com.axolotl.jobmatcher.dto.job;

import com.axolotl.jobmatcher.entity.Job;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobRequest {

    @NotBlank
    private String title;

    private String description;
    private String requirements;
    private Integer salaryMin;
    private Integer salaryMax;
    private String location;
    private Job.JobType jobType;
    private String sourceUrl;
    private LocalDateTime expiredAt;

}