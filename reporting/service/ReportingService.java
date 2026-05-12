package com.abilitybridge.reporting.service;

import com.abilitybridge.exception.*;
import com.abilitybridge.reporting.entity.*;
import com.abilitybridge.reporting.repository.*;
import com.abilitybridge.user.entity.*;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {

    // Auto-flag threshold: flag employer after this many open reports
    private static final long FLAG_THRESHOLD = 3;

    private final DiscriminationReportRepository reportRepo;
    private final EmployerFlagRepository         flagRepo;
    private final UserRepository                 userRepository;

    // ── FR9: File an anonymous discrimination report ───────────
    @Transactional
    public DiscriminationReport fileReport(UUID reporterId, UUID employerId,
                                           String description, String evidenceUrl,
                                           boolean anonymous) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        DiscriminationReport report = DiscriminationReport.builder()
                .reporter(reporter)
                .employer(employer)
                .description(description)
                .evidenceUrl(evidenceUrl)
                .isAnonymous(anonymous)
                .build();

        report = reportRepo.save(report);
        log.info("Discrimination report filed against employer {}", employerId);

        // Auto-flag employer if threshold exceeded
        long openReports = reportRepo.countByEmployerIdAndStatus(employerId, ReportStatus.SUBMITTED);
        if (openReports >= FLAG_THRESHOLD) {
            autoFlagEmployer(employer, reporter, openReports);
        }

        return report;
    }

    // ── FR9: Admin reviews a report ───────────────────────────
    @Transactional
    public DiscriminationReport reviewReport(UUID adminId, UUID reportId, ReportStatus newStatus) {
        DiscriminationReport report = reportRepo.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        report.setStatus(newStatus);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewedBy(admin);
        return reportRepo.save(report);
    }

    // ── FR9: Admin issues warning / removes employer ──────────
    @Transactional
    public void takeActionOnEmployer(UUID adminId, UUID employerId, AccountStatus action, String notes) {
        if (action != AccountStatus.SUSPENDED && action != AccountStatus.REMOVED) {
            throw new BadRequestException("Action must be SUSPENDED or REMOVED");
        }
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        employer.setStatus(action);
        userRepository.save(employer);

        // Resolve all open flags for this employer
        flagRepo.findByEmployerIdAndIsResolvedFalse(employerId).forEach(f -> {
            f.setIsResolved(true);
            f.setResolvedAt(LocalDateTime.now());
            f.setAdminNotes(notes);
            flagRepo.save(f);
        });

        log.info("Admin {} took action {} against employer {}", adminId, action, employerId);
    }

    // ── FR9: Get reports (admin) ──────────────────────────────
    public Page<DiscriminationReport> getAllReports(ReportStatus status, Pageable pageable) {
        if (status != null) return reportRepo.findByStatus(status, pageable);
        return reportRepo.findAll(pageable);
    }

    public Page<DiscriminationReport> getReportsByEmployer(UUID employerId, Pageable pageable) {
        return reportRepo.findByEmployerId(employerId, pageable);
    }

    // ── FR9: Get flagged employers (admin) ────────────────────
    public Page<EmployerFlag> getFlaggedEmployers(Pageable pageable) {
        return flagRepo.findByIsResolvedFalse(pageable);
    }

    // ── Helpers ───────────────────────────────────────────────
    private void autoFlagEmployer(User employer, User flaggedBy, long reportCount) {
        long existingFlags = flagRepo.countByEmployerIdAndIsResolvedFalse(employer.getId());
        if (existingFlags == 0) {
            EmployerFlag flag = EmployerFlag.builder()
                    .employer(employer)
                    .flaggedBy(flaggedBy)
                    .reason("Auto-flagged: " + reportCount + " unreviewed discrimination reports")
                    .build();
            flagRepo.save(flag);
            log.warn("Employer {} auto-flagged for admin review ({} reports)", employer.getId(), reportCount);
        }
    }
}
