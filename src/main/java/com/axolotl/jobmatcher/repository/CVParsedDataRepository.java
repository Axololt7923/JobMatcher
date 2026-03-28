package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.CVParsedData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CVParsedDataRepository extends JpaRepository<CVParsedData, UUID> {
    Optional<CVParsedData> findByCvId(UUID cvId);
}