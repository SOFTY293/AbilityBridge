package com.abilitybridge.reporting.repository;

import com.abilitybridge.reporting.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DiscriminationReportRepository extends JpaRepository<DiscriminationReport, UUID> {
    Page<DiscriminationReport> findByEmployerId(UUID employerId, Pageable pageable);
    Page<DiscriminationReport> findByStatus(ReportStatus status, Pageable pageable);
    long countByEmployerIdAndStatus(UUID employerId, ReportStatus status);
    long countByEmployerId(UUID employerId);
    long countByStatus(ReportStatus status);
}
