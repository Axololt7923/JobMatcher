package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.ai.CVParseResponse;
import com.axolotl.jobmatcher.dto.ai.JobMatchResponse;
import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.exception.AppException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${ai.service.url:http://127.0.0.1:8000}")
    private String aiServiceUrl;

    private final RestClient restClient = RestClient.create();


    public void validateService(){
        ResponseEntity<Map> response = restClient.get()
                .uri(aiServiceUrl + "/health")
                .retrieve()
                .toEntity(Map.class);


        String status = (String) response.getBody().get("status");

        if (status == null) {
            throw new AppException("AI service is not available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (!status.equals("ok")) {
            throw new AppException("AI service is not available", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    public CVParsedResult parseCV(byte[] fileBytes, UUID userId) {

        validateService();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", fileBytes)
                .filename("cv.pdf")
                .contentType(MediaType.APPLICATION_PDF);

        builder.part("user_id", userId.toString());

        CVParseResponse response = restClient.post()
                .uri("http://127.0.0.1:8000/cv_parsed")
                .header("X-API-KEY", "random_api_key_for_testing")//temp hardcode
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .body(CVParseResponse.class);

        if (response == null) {
            throw new AppException("Failed to parse CV", HttpStatus.BAD_REQUEST);
        }

        return CVParsedResult.builder()
                .chromaId(response.getChromaId())
                .skills(response.getSkills().toArray(new String[0]))
                .experienceYears(response.getExperienceYears())
                .educationLevel(response.getEducationLevel())
                .languages(response.getLanguages().toArray(new String[0]))
                .summary(response.getSummary())
                .rawJson(response.toString())
                .build();
    }

    public List<JobMatchResponse> recommendJobs(String chromaId, int topK) {
        Map<String, Object> body = Map.of(
                "chroma_id", chromaId,
                "top_k", topK
        );

        JobMatchResponse[] response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(aiServiceUrl + "/recommend/" + chromaId)
                        .queryParam("top_k", topK)
                        .build())
                .header("X-API-KEY", "random_api_key_for_testing")
                .retrieve()
                .body(JobMatchResponse[].class);

        return response != null ? List.of(response) : List.of();
    }

    public void upsertJob(Job job){
        validateService();
        Map<String, Object> body = Map.of(
                "job_id", job.getId().toString(),
                "title", job.getTitle(),
                "description", job.getDescription() != null ? job.getDescription() : "",
                "requirements", job.getRequirements() != null ? job.getRequirements() : "",
                "company", job.getCompany() != null ? job.getCompany().getName() : "None"
        );
        System.out.println("Body" + body);
        restClient.post()
                .uri(aiServiceUrl + "/jobs")
                .header("X-API-KEY", "random_api_key_for_testing")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public void deletedJobVector(String chromaId){
        try {
            restClient.delete()
                    .uri(aiServiceUrl + "/jobs/" + chromaId)
                    .header("X-API-KEY", "random_api_key_for_testing")
                    .retrieve()
                    .toBodilessEntity();
        }
        catch (Exception e){
            throw new AppException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Data
    @lombok.Builder
    public static class CVParsedResult {
        private String chromaId;
        private String[] skills;
        private Float experienceYears;
        private String educationLevel;
        private String[] languages;
        private String summary;
        private String rawJson;
    }
}