package com.axolotl.jobmatcher.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobMatchResponse {
    @JsonProperty("job_id")
    private String jobId;
    private Float similarity;
}