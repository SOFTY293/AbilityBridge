package com.abilitybridge.mentorship.service;

import com.abilitybridge.exception.*;
import com.abilitybridge.mentorship.entity.*;
import com.abilitybridge.mentorship.repository.*;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipService {

    private final MentorProfileRepository       mentorProfileRepo;
    private final MentorshipRequestRepository   requestRepo;
    private final UserRepository                userRepository;

    // ── FR7: Register as mentor ───────────────────────────────
    @Transactional
    public MentorProfile registerMentor(UUID userId, MentorProfile profileData) {
        if (mentorProfileRepo.existsByUserId(userId)) {
            throw new BadRequestException("You are already registered as a mentor");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        profileData.setUser(user);
        MentorProfile saved = mentorProfileRepo.save(profileData);
        log.info("New mentor registered: {}", userId);
        return saved;
    }

    // ── FR7: Search mentors ───────────────────────────────────
    public Page<MentorProfile> searchMentors(String industry, String careerStage,
                                              String disabilityType, Pageable pageable) {
        return mentorProfileRepo.searchMentors(industry, careerStage, disabilityType, pageable);
    }

    // ── FR7: Get mentor profile ───────────────────────────────
    public MentorProfile getMentorProfile(UUID mentorUserId) {
        return mentorProfileRepo.findByUserId(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));
    }

    // ── FR7: Update mentor profile ────────────────────────────
    @Transactional
    public MentorProfile updateProfile(UUID userId, MentorProfile updates) {
        MentorProfile profile = mentorProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));

        if (updates.getIndustry()      != null) profile.setIndustry(updates.getIndustry());
        if (updates.getBio()           != null) profile.setBio(updates.getBio());
        if (updates.getCareerStage()   != null) profile.setCareerStage(updates.getCareerStage());
        if (updates.getMaxMentees()    != null) profile.setMaxMentees(updates.getMaxMentees());
        if (updates.getIsAvailable()   != null) profile.setIsAvailable(updates.getIsAvailable());
        if (updates.getOffersCvReview()   != null) profile.setOffersCvReview(updates.getOffersCvReview());
        if (updates.getOffersInterview()  != null) profile.setOffersInterview(updates.getOffersInterview());
        if (updates.getOffersCareerAdv()  != null) profile.setOffersCareerAdv(updates.getOffersCareerAdv());

        return mentorProfileRepo.save(profile);
    }

    // ── FR7: Send mentorship request ──────────────────────────
    @Transactional
    public MentorshipRequest sendRequest(UUID menteeId, UUID mentorUserId, String message) {
        if (menteeId.equals(mentorUserId)) {
            throw new BadRequestException("You cannot request mentorship from yourself");
        }
        if (requestRepo.existsByMentorIdAndMenteeId(mentorUserId, menteeId)) {
            throw new BadRequestException("Mentorship request already exists");
        }

        MentorProfile mentorProfile = mentorProfileRepo.findByUserId(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        if (!mentorProfile.getIsAvailable()) {
            throw new BadRequestException("This mentor is not currently available");
        }

        // Check capacity
        long activeMentees = requestRepo.countByMentorIdAndStatus(mentorUserId, MentorshipStatus.ACTIVE);
        if (activeMentees >= mentorProfile.getMaxMentees()) {
            throw new BadRequestException("This mentor has reached their maximum mentee capacity");
        }

        User mentor = userRepository.findById(mentorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor user not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentee not found"));

        MentorshipRequest request = MentorshipRequest.builder()
                .mentor(mentor).mentee(mentee).message(message).build();

        return requestRepo.save(request);
    }

    // ── FR7: Respond to request (mentor accepts/declines) ─────
    @Transactional
    public MentorshipRequest respondToRequest(UUID mentorUserId, UUID requestId, boolean accept) {
        MentorshipRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getMentor().getId().equals(mentorUserId)) {
            throw new ForbiddenException("You are not the mentor for this request");
        }
        if (request.getStatus() != MentorshipStatus.PENDING) {
            throw new BadRequestException("Request has already been responded to");
        }

        request.setStatus(accept ? MentorshipStatus.ACTIVE : MentorshipStatus.DECLINED);
        request.setRespondedAt(LocalDateTime.now());
        return requestRepo.save(request);
    }

    // ── FR7: Get my requests ──────────────────────────────────
    public Page<MentorshipRequest> getMyRequests(UUID userId, String role, Pageable pageable) {
        if ("MENTOR".equals(role)) {
            return requestRepo.findByMentorId(userId, pageable);
        }
        return requestRepo.findByMenteeId(userId, pageable);
    }
}
