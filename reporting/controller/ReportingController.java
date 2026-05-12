package com.abilitybridge.reporting.controller;

import com.abilitybridge.reporting.entity.*;
import com.abilitybridge.reporting.service.ReportingService;
import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.user.entity.AccountStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Discrimination Reporting", description = "Anonymous reporting & employer accountability — FR9")
public class ReportingController {

    private final ReportingService reportingService;
    private final JwtUtil          jwtUtil;

    // ── Any authenticated user can file a report ──────────────
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "File an anonymous discrimination report against an employer")
    public ResponseEntity<DiscriminationReport> fileReport(
            @RequestBody ReportRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reportingService.fileReport(uid(auth), req.getEmployerId(),
                        req.getDescription(), req.getEvidenceUrl(),
                        req.isAnonymous()));
    }

    // ── Admin: view all reports ───────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: list all reports, optionally filtered by status")
    public ResponseEntity<Page<DiscriminationReport>> getAllReports(
            @RequestParam(required = false) ReportStatus status, Pageable pageable) {
        return ResponseEntity.ok(reportingService.getAllReports(status, pageable));
    }

    @GetMapping("/employer/{employerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: get all reports for a specific employer")
    public ResponseEntity<Page<DiscriminationReport>> getByEmployer(
            @PathVariable UUID employerId, Pageable pageable) {
        return ResponseEntity.ok(reportingService.getReportsByEmployer(employerId, pageable));
    }

    // ── Admin: review a report ────────────────────────────────
    @PatchMapping("/{reportId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: update report status (UNDER_REVIEW / ACTIONED / DISMISSED)")
    public ResponseEntity<DiscriminationReport> reviewReport(
            @PathVariable UUID reportId,
            @RequestBody ReviewRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(reportingService.reviewReport(uid(auth), reportId, req.getStatus()));
    }

    // ── Admin: flagged employers ───────────────────────────────
    @GetMapping("/flagged-employers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: list employers flagged for repeated reports")
    public ResponseEntity<Page<EmployerFlag>> getFlaggedEmployers(Pageable pageable) {
        return ResponseEntity.ok(reportingService.getFlaggedEmployers(pageable));
    }

    // ── Admin: take action ────────────────────────────────────
    @PostMapping("/employer/{employerId}/action")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: suspend or remove a flagged employer")
    public ResponseEntity<Void> takeAction(
            @PathVariable UUID employerId,
            @RequestBody ActionRequest req,
            @RequestHeader("Authorization") String auth) {
        reportingService.takeActionOnEmployer(uid(auth), employerId, req.getAction(), req.getNotes());
        return ResponseEntity.noContent().build();
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    @Data static class ReportRequest {
        private UUID employerId;
        @NotBlank private String description;
        private String evidenceUrl;
        private boolean anonymous = true;
    }
    @Data static class ReviewRequest   { private ReportStatus status; }
    @Data static class ActionRequest   { private AccountStatus action; private String notes; }
}
