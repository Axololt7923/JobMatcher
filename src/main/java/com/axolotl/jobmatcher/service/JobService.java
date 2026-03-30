package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.job.JobRequest;
import com.axolotl.jobmatcher.dto.job.JobResponse;
import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.JobRepository;
import com.axolotl.jobmatcher.repository.UserRepository;
import com.axolotl.jobmatcher.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    private final AIService aiService;

    public JobResponse create(JobRequest request, UUID recruiterId) {
        User recruiter = userRepository.findById(recruiterId).orElseThrow(
                () -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND)
        );
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .sourceUrl(request.getSourceUrl())
                .expiredAt(request.getExpiredAt())
                .company(recruiter.getCompany())
                .createdBy(recruiter)
                .build();

        try {
            job = jobRepository.save(job);
            aiService.upsertJob(job);
            return toResponse(jobRepository.save(job));
        }
        catch (Exception e) {
            jobRepository.delete(job);
            throw new AppException("Failed to upsert job", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public JobResponse getById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException("Job doesn't exist", HttpStatus.NOT_FOUND));
        return toResponse(job);
    }

    public List<JobResponse> getAllActivate(int limit, int offset) {

        if (limit > 100 || limit < 0) {
            throw new AppException("Limit must be less than 100 and bigger than 0", HttpStatus.BAD_REQUEST);
        }
        if (offset < 0) {
            throw new AppException("Offset must be bigger than 0", HttpStatus.BAD_REQUEST);
        }

        List<Job> jobs = jobRepository.findByIsActiveTrue();
        int len_jobs = jobs.size();
        int begin_idx = offset > len_jobs ? len_jobs - 1 : offset;
        int end_idx = Math.min(begin_idx + limit, len_jobs);

        return jobs.subList(begin_idx, end_idx)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<JobResponse> getAll( boolean isActive, int limit, int offset) {
        if (isActive) {
            return getAllActivate(limit, offset);
        }
        if (limit > 100 || limit < 0) {
            throw new AppException("Limit must be less than 100 and bigger than 0", HttpStatus.BAD_REQUEST);
        }
        if (offset < 0) {
            throw new AppException("Offset must be bigger than 0", HttpStatus.BAD_REQUEST);
        }

        List<Job> jobs = jobRepository.findAll();

        return Utils.getResponsePage(jobs, offset, limit)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Deprecated
    public List<JobResponse> search(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobResponse update(UUID id, UUID recruiterId, JobRequest request) {

        if (!userRepository.existsById(recruiterId))
            throw new AppException("User doesn't exist", HttpStatus.NOT_FOUND);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException("Job doesn't exist", HttpStatus.NOT_FOUND));

        if (!job.getCreatedBy().getId().equals(recruiterId)) {
            throw new AppException("No permission", HttpStatus.FORBIDDEN);
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSourceUrl(request.getSourceUrl());
        job.setExpiredAt(request.getExpiredAt());

        return toResponse(jobRepository.save(job));
    }

    public void inactivate(UUID id, UUID recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException("Job doesn't exist", HttpStatus.NOT_FOUND));

        if (!job.getCreatedBy().getId().equals(recruiterId)) {
            throw new AppException("No permission", HttpStatus.FORBIDDEN);
        }

        if (!job.getIsActive()) {
            throw new AppException("Inactivated job", HttpStatus.CONFLICT);
        }
        try {
            aiService.deletedJobVector(job.getChromaId());
            job.setIsActive(false);
            aiService.validateService();
            jobRepository.save(job);
        }
        catch (Exception e) {
            throw new AppException("Failed to inactivate job", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .isActive(job.getIsActive())
                .sourceUrl(job.getSourceUrl())
                .createdAt(job.getCreatedAt())
                .expiredAt(job.getExpiredAt())
                .createdBy(job.getCreatedBy().getId())
                .contactEmail(resolveContactEmail(job))
                .companyId(job.getCompany() != null ? job.getCompany().getId() : null)
                .companyName(job.getCompany() != null ? job.getCompany().getName() : null)
                .build();
    }

    private String resolveContactEmail(Job job) {
        if (job.getCompany() != null && job.getCompany().getContactEmail() != null) {
            return job.getCompany().getContactEmail();
        }
        return job.getCreatedBy() != null ? job.getCreatedBy().getEmail() : null;
    }


}