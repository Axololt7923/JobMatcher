package com.axolotl.jobmatcher.scheduler;

import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.JobRepository;
import com.axolotl.jobmatcher.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobRepository jobRepository;
    private final AIService aiService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void inactivateExpiredJobs() {
        List<Job> expiredJobs = jobRepository.findExpiredJobs(LocalDateTime.now());

        for (Job job : expiredJobs) {
            try {
                job.setIsActive(false);
                aiService.deletedJobVector(job.getChromaId());
            } catch (Exception e) {
                System.out.println("Failed to delete job vector");
                throw new AppException("internal delete job fail", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        jobRepository.saveAll(expiredJobs);

        System.out.println("Inactivate " + expiredJobs.size() + " expired jobs");
    }
}