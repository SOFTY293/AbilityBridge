package com.abilitybridge.accommodation.service;

import com.abilitybridge.accommodation.entity.*;
import com.abilitybridge.accommodation.repository.*;
import com.abilitybridge.exception.*;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationService {

    private final AccommodationNeedRepository       needRepo;
    private final AccommodationNegotiationRepository negotiationRepo;
    private final AccommodationAgreementRepository  agreementRepo;
    private final UserRepository                    userRepository;

    // ── FR5: Add accommodation need ───────────────────────────
    @Transactional
    public AccommodationNeed addNeed(UUID userId, String needType, String description, boolean mandatory) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AccommodationNeed need = AccommodationNeed.builder()
                .user(user).needType(needType)
                .description(description).isMandatory(mandatory).build();
        return needRepo.save(need);
    }

    // ── FR5: Get seeker's needs ───────────────────────────────
    public List<AccommodationNeed> getSeekerNeeds(UUID userId) {
        return needRepo.findByUserId(userId);
    }

    // ── FR5: Check compatibility ──────────────────────────────
    public BigDecimal checkCompatibility(UUID seekerId, UUID employerId, UUID jobId) {
        List<AccommodationNeed> seekerNeeds = needRepo.findByUserIdAndIsMandatoryTrue(seekerId);
        if (seekerNeeds.isEmpty()) return BigDecimal.valueOf(100);

        // Simplified: each need matched = 100/total needs score
        // In production, compare against employer's accommodationsOffered field
        int totalNeeds = seekerNeeds.size();
        int matched = 0; // Would be computed against employer's offered accommodations

        double score = totalNeeds > 0 ? (double) matched / totalNeeds * 100 : 100.0;
        return BigDecimal.valueOf(Math.round(score * 10) / 10.0);
    }

    // ── FR5: Open negotiation channel ─────────────────────────
    @Transactional
    public AccommodationNegotiation openNegotiation(UUID seekerId, UUID employerId, UUID jobId) {
        User seeker   = userRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker not found"));
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        BigDecimal compatibility = checkCompatibility(seekerId, employerId, jobId);

        AccommodationNegotiation negotiation = AccommodationNegotiation.builder()
                .seeker(seeker).employer(employer)
                .jobId(jobId).compatibility(compatibility)
                .status(NegotiationStatus.OPEN).build();

        return negotiationRepo.save(negotiation);
    }

    // ── FR5: Log agreement ────────────────────────────────────
    @Transactional
    public AccommodationAgreement logAgreement(UUID userId, UUID negotiationId, String terms) {
        AccommodationNegotiation negotiation = negotiationRepo.findById(negotiationId)
                .orElseThrow(() -> new ResourceNotFoundException("Negotiation not found"));

        boolean isSeeker   = negotiation.getSeeker().getId().equals(userId);
        boolean isEmployer = negotiation.getEmployer().getId().equals(userId);
        if (!isSeeker && !isEmployer) {
            throw new ForbiddenException("You are not a party to this negotiation");
        }

        AccommodationAgreement agreement = AccommodationAgreement.builder()
                .negotiation(negotiation)
                .agreedTerms(terms)
                .seekerSigned(isSeeker)
                .employerSigned(isEmployer)
                .build();

        negotiation.setStatus(NegotiationStatus.AGREED);
        negotiation.setResolvedAt(java.time.LocalDateTime.now());
        negotiationRepo.save(negotiation);

        log.info("Accommodation agreement logged for negotiation {}", negotiationId);
        return agreementRepo.save(agreement);
    }

    // ── FR5: Get negotiations ─────────────────────────────────
    public List<AccommodationNegotiation> getUserNegotiations(UUID userId) {
        List<AccommodationNegotiation> all = new ArrayList<>();
        all.addAll(negotiationRepo.findBySeekerId(userId));
        all.addAll(negotiationRepo.findByEmployerId(userId));
        return all;
    }
}
