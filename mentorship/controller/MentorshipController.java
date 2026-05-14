package com.abilitybridge.mentorship.controller;

import com.abilitybridge.mentorship.entity.*;
import com.abilitybridge.mentorship.service.MentorshipService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Mentorship Network", description = "Register, search, and connect with mentors — FR7")
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final JwtUtil           jwtUtil;

    // ── Public search ─────────────────────────────────────────
    @GetMapping("/mentors")
    @Operation(summary = "Search mentors by industry, career stage, or disability type")
    public ResponseEntity<Page<MentorProfile>> searchMentors(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String careerStage,
            @RequestParam(required = false) String disabilityType,
            Pageable pageable) {
        return ResponseEntity.ok(mentorshipService.searchMentors(industry, careerStage, disabilityType, pageable));
    }

    @GetMapping("/mentors/{mentorUserId}")
    @Operation(summary = "Get a mentor's public profile")
    public ResponseEntity<MentorProfile> getMentorProfile(@PathVariable UUID mentorUserId) {
        return ResponseEntity.ok(mentorshipService.getMentorProfile(mentorUserId));
    }

    // ── Mentor registration & profile ─────────────────────────
    @PostMapping("/register-mentor")
    @Operation(summary = "Register as a mentor (Mentor role required)")
    public ResponseEntity<MentorProfile> registerMentor(
            @RequestBody MentorProfile profileData,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorshipService.registerMentor(userId(auth), profileData));
    }

    @PatchMapping("/my-profile")
    @Operation(summary = "Update your mentor profile")
    public ResponseEntity<MentorProfile> updateProfile(
            @RequestBody MentorProfile updates,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(mentorshipService.updateProfile(userId(auth), updates));
    }

    // ── Requests ──────────────────────────────────────────────
    @PostMapping("/mentors/{mentorUserId}/request")
    @Operation(summary = "Send a mentorship request")
    public ResponseEntity<MentorshipRequest> sendRequest(
            @PathVariable UUID mentorUserId,
            @org.springframework.web.bind.annotation.RequestBody RequestMessageDTO body, // Explicitly use Spring's annotation here
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorshipService.sendRequest(userId(auth), mentorUserId, body.getMessage()));
    }

    @PatchMapping("/requests/{requestId}/respond")
    @Operation(summary = "Accept or decline a mentorship request (mentor only)")
    public ResponseEntity<MentorshipRequest> respond(
            @PathVariable UUID requestId,
            @org.springframework.web.bind.annotation.RequestBody RespondDTO body, // Explicitly use Spring's annotation here
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(mentorshipService.respondToRequest(userId(auth), requestId, body.isAccept()));
    }
    private UUID userId(String auth) {
        return jwtUtil.extractUserId(auth.substring(7));
    }

    // Rename these classes to avoid conflict with Spring Annotations
    @Data static class RequestMessageDTO { private String message; }
    @Data static class RespondDTO { private boolean accept; }
}