package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByIsActiveTrue(Pageable pageable);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findAllByIdIn(List<UUID> ids);

    @Query("SELECT j FROM Job j WHERE j.expiredAt < :now AND j.isActive = true")
    List<Job> findExpiredJobs(@Param("now") LocalDateTime now);
}
