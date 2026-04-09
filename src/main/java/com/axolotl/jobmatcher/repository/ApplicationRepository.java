package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Deprecated(forRemoval = false)
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByUserId(UUID userId);

    List<Application> findByJobId(UUID jobId);

    boolean existsByUserIdAndJobId(UUID userId, UUID jobId);
}