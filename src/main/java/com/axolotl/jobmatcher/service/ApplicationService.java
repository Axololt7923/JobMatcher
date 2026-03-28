package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.application.ApplicationRequest;
import com.axolotl.jobmatcher.dto.application.ApplicationResponse;
import com.axolotl.jobmatcher.entity.Application;
import com.axolotl.jobmatcher.entity.CV;
import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.ApplicationRepository;
import com.axolotl.jobmatcher.repository.CVRepository;
import com.axolotl.jobmatcher.repository.JobRepository;
import com.axolotl.jobmatcher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Deprecated(forRemoval = false)
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CVRepository cvRepository;

    public ApplicationResponse apply(UUID userId, ApplicationRequest request) {
        if (applicationRepository.existsByUserIdAndJobId(userId, request.getJobId())) {
            throw new AppException("You have applied for this job", HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new AppException("Job doesn't exist", HttpStatus.NOT_FOUND));

        CV cv = cvRepository.findById(request.getCvId())
                .orElseThrow(() -> new AppException("CV doesn't exist", HttpStatus.NOT_FOUND));

        Application application = Application.builder()
                .user(user)
                .job(job)
                .cv(cv)
                .status(Application.Status.applied)
                .build();

        return toResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getByUserId(UUID userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ApplicationResponse> getByJobId(UUID jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApplicationResponse updateStatus(UUID id, Application.Status status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException("Application doesn't exist", HttpStatus.NOT_FOUND));

        application.setStatus(status);
        return toResponse(applicationRepository.save(application));
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .userId(application.getUser().getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompany() != null
                        ? application.getJob().getCompany().getName() : null)
                .cvId(application.getCv().getId())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}