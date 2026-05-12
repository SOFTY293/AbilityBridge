package com.abilitybridge.reporting.repository;

import com.abilitybridge.reporting.entity.EmployerFlag;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployerFlagRepository extends JpaRepository<EmployerFlag, UUID> {
    List<EmployerFlag> findByEmployerIdAndIsResolvedFalse(UUID employerId);
    Page<EmployerFlag> findByIsResolvedFalse(Pageable pageable);
    long countByEmployerIdAndIsResolvedFalse(UUID employerId);
    long countByIsResolvedFalse();
}
