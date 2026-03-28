package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.ai.JobMatchResponse;
import com.axolotl.jobmatcher.dto.job.JobResponse;
import com.axolotl.jobmatcher.entity.CV;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.CVRepository;
//import com.axolotl.jobmatcher.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final AIService aiService;
    private final CVRepository cvRepository;
//    private final JobRepository jobRepository;
    private final JobService jobService;

    public List<JobResponse> recommendJobs(UUID userId, int topK) {

        CV cv = cvRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new AppException(
                        "Upload your CV", HttpStatus.BAD_REQUEST));

        if (cv.getChromaId() == null) {
            throw new AppException(
                    "CV have not been parsed yet", HttpStatus.BAD_REQUEST);
        }

        List<JobMatchResponse> matches = aiService.recommendJobs(cv.getChromaId(), topK);

        return matches.stream()
                .map(match -> {
                    try{
                        return jobService.getById(UUID.fromString(match.getJobId()));}
                    catch (Exception e){
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}