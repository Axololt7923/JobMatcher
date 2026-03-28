package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.Job;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByIsActiveTrue();
    List<Job> findByTitleContainingIgnoreCase(String title);
    @Query("SELECT j FROM Job j WHERE j.expiredAt < :now AND j.isActive = true")
    List<Job> findExpiredJobs(@Param("now") LocalDateTime now);
}
