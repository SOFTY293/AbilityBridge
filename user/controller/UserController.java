package com.abilitybridge.user.controller;

import com.abilitybridge.profile.dto.AccessibilitySettingsDto;
import com.abilitybridge.profile.service.ProfileService;
import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.user.dto.UserSummaryDto;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import com.abilitybridge.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Management", description = "Account info, accessibility settings — FR1")
public class UserController {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final JwtUtil        jwtUtil;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's summary")
    public ResponseEntity<UserSummaryDto> me(@RequestHeader("Authorization") String auth) {
        UUID userId = uid(auth);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .build());
    }

    @GetMapping("/me/accessibility")
    @Operation(summary = "Get my accessibility settings — FR1")
    public ResponseEntity<AccessibilitySettingsDto> getAccessibility(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(profileService.getAccessibilitySettings(uid(auth)));
    }

    @PutMapping("/me/accessibility")
    @Operation(summary = "Update my accessibility settings — FR1")
    public ResponseEntity<AccessibilitySettingsDto> updateAccessibility(
            @RequestBody AccessibilitySettingsDto dto,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(profileService.updateAccessibilitySettings(uid(auth), dto));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }
}
