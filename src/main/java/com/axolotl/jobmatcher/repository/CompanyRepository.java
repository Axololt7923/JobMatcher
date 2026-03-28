package com.axolotl.jobmatcher.repository;

import com.axolotl.jobmatcher.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByNameContainingIgnoreCase(String name);
//    Company getCompanyById(UUID id);
    boolean existsByName(String name);
    boolean existsByContactEmail(String email);
}