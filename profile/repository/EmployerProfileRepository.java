package com.abilitybridge.profile.repository;
import com.abilitybridge.profile.entity.EmployerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, UUID> {
    Optional<EmployerProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Page<EmployerProfile> findByIsVerified(Boolean isVerified, Pageable pageable);
}
