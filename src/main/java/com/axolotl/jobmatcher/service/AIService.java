package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.ai.CVParseResponse;
import com.axolotl.jobmatcher.dto.ai.JobMatchResponse;
import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.exception.AppException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestClient restClient;

    public void validateService() {
        restClient.get()
                .uri("/health")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new AppException(response.toString(), response.getStatusCode());
                })
                .toBodilessEntity();
    }

    public CVParsedResult parseCV(byte[] fileBytes, UUID userId) {

        validateService();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("file", fileBytes)
                .filename("cv.pdf")
                .contentType(MediaType.APPLICATION_PDF);

        builder.part("user_id", userId.toString());

        CVParseResponse response = restClient.post()
                .uri("/cv_parsed")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, serverResponse) ->
                        handleClientException(serverResponse.getStatusCode(), "parse CV"))
                .requiredBody(CVParseResponse.class);

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
        validateService();

        JobMatchResponse[] serverResponse = restClient.get()
                .uri("/recommend/{chromaId}?top_k={topK}", chromaId, topK)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) ->
                        handleClientException(response.getStatusCode(), "recommend jobs"))
                .body(JobMatchResponse[].class);

        return serverResponse != null ? List.of(serverResponse) : List.of();
    }

    public void upsertJob(Job job) {
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
                .uri("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) ->
                        handleClientException(response.getStatusCode(), "upsert job"))
                .toBodilessEntity();

    }

    public void deletedJobVector(String chromaId) {
        validateService();

        restClient.delete()
                .uri("/jobs/" + chromaId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) ->
                        handleClientException(response.getStatusCode(), "delete job vector"))
                .toBodilessEntity();
    }

    public void deleteCVParsedVectors(String chromaId) {
        validateService();
        restClient.delete()
                .uri("/cv_parsed/" + chromaId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new AppException(response.toString(), response.getStatusCode());
                })
                .toBodilessEntity();
    }

    private void handleClientException(HttpStatusCode status, String context) {
        throw new AppException("AI Service Error during " + context + ". Status: " + status, status);
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