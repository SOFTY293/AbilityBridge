package com.abilitybridge.admin.controller;

import com.abilitybridge.admin.dto.ImpactReportDto;
import com.abilitybridge.admin.dto.UserAdminDto;
import com.abilitybridge.admin.service.AdminService;
import com.abilitybridge.profile.entity.EmployerProfile;
import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.user.entity.AccountStatus;
import com.abilitybridge.user.entity.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Panel", description = "Platform analytics, user management, verifications — FR10")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil      jwtUtil;

    // ── FR10: Impact report ───────────────────────────────────
    @GetMapping("/impact-report")
    @Operation(summary = "Generate platform-wide impact report")
    public ResponseEntity<ImpactReportDto> getImpactReport() {
        return ResponseEntity.ok(adminService.generateImpactReport());
    }

    // ── FR10: User management ─────────────────────────────────
    @GetMapping("/users")
    @Operation(summary = "List all users with optional filters")
    public ResponseEntity<Page<UserAdminDto>> listUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) AccountStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(adminService.listUsers(role, status, pageable));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get a specific user's detail")
    public ResponseEntity<UserAdminDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminService.getUserDetail(userId));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Verify, suspend, or remove a user account")
    public ResponseEntity<UserAdminDto> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody StatusUpdateRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(adminService.updateUserStatus(uid(auth), userId, req.getStatus()));
    }

    // ── FR10: Employer verification ───────────────────────────
    @PostMapping("/employers/{employerUserId}/verify")
    @Operation(summary = "Verify an employer's profile")
    public ResponseEntity<EmployerProfile> verifyEmployer(
            @PathVariable UUID employerUserId,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(adminService.verifyEmployer(uid(auth), employerUserId));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    @Data static class StatusUpdateRequest { private AccountStatus status; }
}
