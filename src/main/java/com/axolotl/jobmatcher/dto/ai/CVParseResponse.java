package com.axolotl.jobmatcher.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CVParseResponse {
    @JsonProperty("chroma_id")
    private String chromaId;

    private List<String> skills;

    @JsonProperty("experience_years")
    private Float experienceYears;

    @JsonProperty("education_level")
    private String educationLevel;

    private List<String> languages;
    private String summary;
}
