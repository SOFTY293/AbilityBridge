package com.abilitybridge.employer.controller;

import com.abilitybridge.employer.entity.*;
import com.abilitybridge.employer.service.EmployerAccountabilityService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/employers")
@RequiredArgsConstructor
@Tag(name = "Employer Accountability", description = "Reality scores, badges, community ratings — FR6")
public class EmployerController {

    private final EmployerAccountabilityService accountabilityService;
    private final JwtUtil jwtUtil;

    // ── Public ────────────────────────────────────────────────
    @GetMapping("/public/{employerId}/dashboard")
    @Operation(summary = "View employer's public accountability dashboard")
    public ResponseEntity<Map<String, Object>> getPublicDashboard(@PathVariable UUID employerId) {
        return ResponseEntity.ok(accountabilityService.getPublicDashboard(employerId));
    }

    @GetMapping("/public/{employerId}/score")
    @Operation(summary = "Get Workplace Reality Score for an employer")
    public ResponseEntity<WorkplaceRealityScore> getScore(@PathVariable UUID employerId) {
        return ResponseEntity.ok(accountabilityService.getScore(employerId));
    }

    @GetMapping("/public/{employerId}/ratings")
    @Operation(summary = "Get community ratings for an employer")
    public ResponseEntity<Page<EmployerRating>> getRatings(
            @PathVariable UUID employerId, Pageable pageable) {
        return ResponseEntity.ok(accountabilityService.getRatings(employerId, pageable));
    }

    // ── Authenticated: submit anonymous rating ────────────────
    @PostMapping("/{employerId}/rate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit an anonymous inclusivity rating for an employer")
    public ResponseEntity<EmployerRating> submitRating(
            @PathVariable UUID employerId,
            @RequestBody RatingRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                accountabilityService.submitRating(uid(auth), employerId,
                        req.getScore(), req.getComment(), req.isAnonymous()));
    }

    // ── Admin only: award badge ───────────────────────────────
    @PostMapping("/{employerId}/badges")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: award an inclusivity badge (Bronze / Silver / Gold)")
    public ResponseEntity<InclusivityBadge> awardBadge(
            @PathVariable UUID employerId,
            @RequestBody BadgeRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                accountabilityService.awardBadge(uid(auth), employerId, req.getTier()));
    }

    // ── Admin only: recompute score ───────────────────────────
    @PostMapping("/{employerId}/score/recompute")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: recompute Workplace Reality Score")
    public ResponseEntity<WorkplaceRealityScore> recompute(
            @PathVariable UUID employerId,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(accountabilityService.recomputeScore(employerId));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    @Data static class RatingRequest { private int score; private String comment; private boolean anonymous = true; }
    @Data static class BadgeRequest  { private BadgeTier tier; }
}
