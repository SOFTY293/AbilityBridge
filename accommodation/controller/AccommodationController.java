package com.abilitybridge.accommodation.controller;

import com.abilitybridge.accommodation.entity.*;
import com.abilitybridge.accommodation.service.AccommodationService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/accommodations")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Accommodation Negotiation", description = "Specify needs, check compatibility, negotiate — FR5")
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final JwtUtil jwtUtil;

    @PostMapping("/needs")
    @Operation(summary = "Add an accommodation need")
    public ResponseEntity<AccommodationNeed> addNeed(
            @RequestBody NeedRequest req,
            @RequestHeader("Authorization") String auth) {
        UUID userId = userId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accommodationService.addNeed(userId, req.getNeedType(),
                        req.getDescription(), req.isMandatory()));
    }

    @GetMapping("/needs")
    @Operation(summary = "Get my accommodation needs")
    public ResponseEntity<List<AccommodationNeed>> getNeeds(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(accommodationService.getSeekerNeeds(userId(auth)));
    }

    @GetMapping("/compatibility")
    @Operation(summary = "Check compatibility with an employer's accommodations")
    public ResponseEntity<Map<String, Object>> checkCompatibility(
            @RequestParam UUID employerId,
            @RequestParam(required = false) UUID jobId,
            @RequestHeader("Authorization") String auth) {
        UUID seekerId = userId(auth);
        BigDecimal score = accommodationService.checkCompatibility(seekerId, employerId, jobId);
        return ResponseEntity.ok(Map.of(
                "seekerId", seekerId,
                "employerId", employerId,
                "compatibilityScore", score,
                "compatible", score.doubleValue() >= 70
        ));
    }

    @PostMapping("/negotiations")
    @Operation(summary = "Open a private negotiation channel with an employer")
    public ResponseEntity<AccommodationNegotiation> openNegotiation(
            @RequestBody NegotiationRequest req,
            @RequestHeader("Authorization") String auth) {
        UUID seekerId = userId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accommodationService.openNegotiation(seekerId, req.getEmployerId(), req.getJobId()));
    }

    @PostMapping("/negotiations/{negotiationId}/agree")
    @Operation(summary = "Log an accommodation agreement")
    public ResponseEntity<AccommodationAgreement> logAgreement(
            @PathVariable UUID negotiationId,
            @RequestBody AgreementRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accommodationService.logAgreement(userId(auth), negotiationId, req.getTerms()));
    }

    @GetMapping("/negotiations")
    @Operation(summary = "Get all my accommodation negotiations")
    public ResponseEntity<List<AccommodationNegotiation>> getNegotiations(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(accommodationService.getUserNegotiations(userId(auth)));
    }

    private UUID userId(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    // ── Inner request DTOs ────────────────────────────────────
    @Data static class NeedRequest {
        @NotBlank private String needType;
        private String description;
        private boolean mandatory = true;
    }
    @Data static class NegotiationRequest {
        private UUID employerId;
        private UUID jobId;
    }
    @Data static class AgreementRequest {
        @NotBlank private String terms;
    }
}
