package com.abilitybridge.employer.repository;

import com.abilitybridge.employer.entity.InclusivityBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InclusivityBadgeRepository extends JpaRepository<InclusivityBadge, UUID> {
    List<InclusivityBadge> findByEmployerIdAndIsActiveTrue(UUID employerId);
    long countByIsActiveTrue();
}
