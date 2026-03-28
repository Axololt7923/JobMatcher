package com.axolotl.jobmatcher.scheduler;

import com.axolotl.jobmatcher.entity.Job;
import com.axolotl.jobmatcher.repository.JobRepository;
import com.axolotl.jobmatcher.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobRepository jobRepository;
    private final AIService aiService;

    @Scheduled(cron = "0 0 * * * *")
    public void inactivateExpiredJobs() {
        List<Job> expiredJobs = jobRepository.findExpiredJobs(LocalDateTime.now());

        for (Job job : expiredJobs){
            job.setIsActive(false);
            try{
                aiService.deletedJobVector(job.getChromaId());
            }
            catch (Exception e){
                System.out.println("Failed to delete job vector");
            }
        }

        jobRepository.saveAll(expiredJobs);

        System.out.println("Inactivate " + expiredJobs.size() + " expired jobs");
    }
}