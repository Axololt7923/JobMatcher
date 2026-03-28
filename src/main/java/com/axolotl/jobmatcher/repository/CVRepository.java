package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CVRepository extends JpaRepository<CV, UUID> {
    List<CV> findByUserId(UUID userId);
    Optional<CV> findByUserIdAndIsActiveTrue(UUID userId);
//    Optional<CV> findByChromaId(String chromaId);
}