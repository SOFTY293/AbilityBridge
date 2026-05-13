package com.abilitybridge.profile.service;

import com.abilitybridge.exception.ForbiddenException;
import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.profile.dto.*;
import com.abilitybridge.profile.entity.*;
import com.abilitybridge.profile.repository.*;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.entity.UserRole;
import com.abilitybridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository                userRepository;
    private final SeekerProfileRepository       seekerProfileRepo;
    private final EmployerProfileRepository     employerProfileRepo;
    private final SeekerDisabilityInfoRepository disabilityInfoRepo;
    private final AccessibilitySettingsRepository accessibilityRepo;

    // ── Seeker Profile ────────────────────────────────────────
    @Transactional
    public SeekerProfileDto createOrUpdateSeekerProfile(UUID userId, SeekerProfileRequest req) {
        User user = getUser(userId);
        if (user.getRole() != UserRole.SEEKER && user.getRole() != UserRole.MENTOR) {
            throw new ForbiddenException("Only seekers and mentors can create seeker profiles");
        }

        SeekerProfile profile = seekerProfileRepo.findByUserId(userId)
                .orElse(SeekerProfile.builder().user(user).build());

        profile.setFullName(req.getFullName());
        profile.setHeadline(req.getHeadline());
        profile.setBio(req.getBio());
        profile.setLocation(req.getLocation());
        profile.setTargetJobCategory(req.getTargetJobCategory());
        profile.setAvailability(req.getAvailability());

        return toDto(seekerProfileRepo.save(profile));
    }

    @Transactional
    public void toggleAnonymousMode(UUID userId, boolean enabled) {
        SeekerProfile profile = seekerProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile not found"));
        profile.setAnonymousMode(enabled);
        seekerProfileRepo.save(profile);
    }

    @Transactional(readOnly = true)
    public SeekerProfileDto getSeekerProfile(UUID userId) {
        return toDto(seekerProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile", userId)));
    }

    // ── Disability Info (private — never exposed in general responses) ──
    @Transactional
    public void saveDisabilityInfo(UUID userId, DisabilityInfoRequest req) {
        User user = getUser(userId);
        SeekerDisabilityInfo info = disabilityInfoRepo.findByUserId(userId)
                .orElse(SeekerDisabilityInfo.builder().user(user).build());
        info.setDisabilityType(req.getDisabilityType());
        info.setDisclosureLevel(req.getDisclosureLevel());
        info.setSupportNeeds(req.getSupportNeeds());
        disabilityInfoRepo.save(info);
    }

    // ── Employer Profile ──────────────────────────────────────
    @Transactional
    public EmployerProfileDto createOrUpdateEmployerProfile(UUID userId, EmployerProfileRequest req) {
        User user = getUser(userId);
        if (user.getRole() != UserRole.EMPLOYER) {
            throw new ForbiddenException("Only employers can create employer profiles");
        }
        EmployerProfile profile = employerProfileRepo.findByUserId(userId)
                .orElse(EmployerProfile.builder().user(user).build());

        profile.setCompanyName(req.getCompanyName());
        profile.setIndustry(req.getIndustry());
        profile.setCompanySize(req.getCompanySize());
        profile.setWebsite(req.getWebsite());
        profile.setDescription(req.getDescription());
        profile.setLocation(req.getLocation());

        return toEmployerDto(employerProfileRepo.save(profile));
    }

    @Transactional(readOnly = true)
    public EmployerProfileDto getEmployerProfile(UUID userId) {
        return toEmployerDto(employerProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile", userId)));
    }

    // ── Accessibility Settings ────────────────────────────────
    @Transactional
    public AccessibilitySettingsDto updateAccessibilitySettings(UUID userId, AccessibilitySettingsDto req) {
        User user = getUser(userId);
        AccessibilitySettings settings = accessibilityRepo.findByUserId(userId)
                .orElse(AccessibilitySettings.builder().user(user).build());

        settings.setScreenReader(req.getScreenReader());
        settings.setHighContrast(req.getHighContrast());
        settings.setVoiceNav(req.getVoiceNav());
        settings.setFontSize(req.getFontSize());
        settings.setDyslexiaFont(req.getDyslexiaFont());
        settings.setSignLangVideo(req.getSignLangVideo());

        return toSettingsDto(accessibilityRepo.save(settings));
    }

    @Transactional(readOnly = true)
    public AccessibilitySettingsDto getAccessibilitySettings(UUID userId) {
        return accessibilityRepo.findByUserId(userId)
                .map(this::toSettingsDto)
                .orElse(new AccessibilitySettingsDto()); // return defaults if not yet set
    }

    // ── Mappers ───────────────────────────────────────────────
    private SeekerProfileDto toDto(SeekerProfile p) {
        return SeekerProfileDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .fullName(p.getFullName())
                .headline(p.getHeadline())
                .bio(p.getBio())
                .location(p.getLocation())
                .profilePictureUrl(p.getProfilePictureUrl())
                .cvUrl(p.getCvUrl())
                .anonymousMode(p.getAnonymousMode())
                .targetJobCategory(p.getTargetJobCategory())
                .availability(p.getAvailability())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private EmployerProfileDto toEmployerDto(EmployerProfile p) {
        return EmployerProfileDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .companyName(p.getCompanyName())
                .industry(p.getIndustry())
                .companySize(p.getCompanySize())
                .website(p.getWebsite())
                .description(p.getDescription())
                .location(p.getLocation())
                .isVerified(p.getIsVerified())
                .build();
    }

    private AccessibilitySettingsDto toSettingsDto(AccessibilitySettings s) {
        AccessibilitySettingsDto dto = new AccessibilitySettingsDto();
        dto.setScreenReader(s.getScreenReader());
        dto.setHighContrast(s.getHighContrast());
        dto.setVoiceNav(s.getVoiceNav());
        dto.setFontSize(s.getFontSize());
        dto.setDyslexiaFont(s.getDyslexiaFont());
        dto.setSignLangVideo(s.getSignLangVideo());
        return dto;
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
