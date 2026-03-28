package com.axolotl.jobmatcher.repository;

// repository/UserRepository.java
import com.axolotl.jobmatcher.entity.Company;
import com.axolotl.jobmatcher.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCompanyId(UUID companyId);
    boolean existsById(UUID id);

    @Query("SELECT c FROM User u JOIN u.company c WHERE u.id = :userId")
    Optional<Company> findCompanyByUserId(@Param("userId") UUID userId);

//    User findUserById(UUID id);

}
