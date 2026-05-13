package com.abilitybridge.profile.controller;

import com.abilitybridge.profile.dto.*;
import com.abilitybridge.profile.service.ProfileService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "Seeker and employer profiles, accessibility settings — FR1")
public class ProfileController {

    private final ProfileService profileService;

    // ── Seeker Profile ────────────────────────────────────────
    @GetMapping("/seeker/{userId}")
    @Operation(summary = "Get seeker profile by user ID")
    public ResponseEntity<SeekerProfileDto> getSeekerProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getSeekerProfile(userId));
    }

    @PutMapping("/seeker/{userId}")
    @PreAuthorize("hasAnyRole('SEEKER','MENTOR') and #userId == authentication.principal.id")
    @Operation(summary = "Create or update seeker profile (ability-first)")
    public ResponseEntity<SeekerProfileDto> upsertSeekerProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody SeekerProfileRequest req) {
        return ResponseEntity.ok(profileService.createOrUpdateSeekerProfile(userId, req));
    }

    @PatchMapping("/seeker/{userId}/anonymous-mode")
    @Operation(summary = "Toggle Anonymous Apply Mode on/off — FR1")
    public ResponseEntity<Void> toggleAnonymousMode(
            @PathVariable UUID userId,
            @RequestParam boolean enabled) {
        profileService.toggleAnonymousMode(userId, enabled);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/seeker/{userId}/disability-info")
    @Operation(summary = "Save private disability information — stored separately per NFR3")
    public ResponseEntity<Void> saveDisabilityInfo(
            @PathVariable UUID userId,
            @RequestBody DisabilityInfoRequest req) {
        profileService.saveDisabilityInfo(userId, req);
        return ResponseEntity.noContent().build();
    }

    // ── Employer Profile ──────────────────────────────────────
    @GetMapping("/employer/{userId}")
    @Operation(summary = "Get employer / company profile")
    public ResponseEntity<EmployerProfileDto> getEmployerProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getEmployerProfile(userId));
    }

    @PutMapping("/employer/{userId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Create or update employer company profile")
    public ResponseEntity<EmployerProfileDto> upsertEmployerProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody EmployerProfileRequest req) {
        return ResponseEntity.ok(profileService.createOrUpdateEmployerProfile(userId, req));
    }

    // ── Accessibility Settings ────────────────────────────────
    @GetMapping("/{userId}/accessibility")
    @Operation(summary = "Get accessibility settings for user — FR1, NFR1")
    public ResponseEntity<AccessibilitySettingsDto> getAccessibility(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getAccessibilitySettings(userId));
    }

    @PutMapping("/{userId}/accessibility")
    @Operation(summary = "Update accessibility settings — FR1, NFR1")
    public ResponseEntity<AccessibilitySettingsDto> updateAccessibility(
            @PathVariable UUID userId,
            @RequestBody AccessibilitySettingsDto req) {
        return ResponseEntity.ok(profileService.updateAccessibilitySettings(userId, req));
    }
}
