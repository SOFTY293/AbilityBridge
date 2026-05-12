package com.abilitybridge.employer.service;

import com.abilitybridge.employer.entity.*;
import com.abilitybridge.employer.repository.*;
import com.abilitybridge.exception.*;
import com.abilitybridge.profile.repository.EmployerProfileRepository;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerAccountabilityService {

    private final WorkplaceRealityScoreRepository scoreRepo;
    private final InclusivityBadgeRepository      badgeRepo;
    private final EmployerRatingRepository        ratingRepo;
    private final EmployerProfileRepository       employerProfileRepo;
    private final UserRepository                  userRepository;

    // ── FR6: Get Workplace Reality Score ─────────────────────
    public WorkplaceRealityScore getScore(UUID employerId) {
        return scoreRepo.findByEmployerId(employerId)
                .orElseGet(() -> createDefaultScore(employerId));
    }

    // ── FR6: Recompute score (called by scheduler or admin) ───
    @Transactional
    public WorkplaceRealityScore recomputeScore(UUID employerId) {
        WorkplaceRealityScore score = scoreRepo.findByEmployerId(employerId)
                .orElseGet(() -> createDefaultScore(employerId));

        Double communityAvg = ratingRepo.averageScoreByEmployer(employerId);
        BigDecimal communityScore = communityAvg != null
                ? BigDecimal.valueOf(communityAvg / 5 * 100).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        score.setCommunityScore(communityScore);

        // Overall = weighted average of sub-scores
        BigDecimal overall = score.getHiringScore()
                .add(score.getAccommodationScore())
                .add(communityScore)
                .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
        score.setOverallScore(overall);
        score.setComputedAt(LocalDateTime.now());

        return scoreRepo.save(score);
    }

    // ── FR6: Public employer dashboard ────────────────────────
    public Map<String, Object> getPublicDashboard(UUID employerId) {
        WorkplaceRealityScore score = getScore(employerId);
        List<InclusivityBadge> badges = badgeRepo.findByEmployerIdAndIsActiveTrue(employerId);
        long ratingCount = ratingRepo.countByEmployerId(employerId);
        Double avgRating = ratingRepo.averageScoreByEmployer(employerId);

        return Map.of(
                "employerId", employerId,
                "workplaceRealityScore", score.getOverallScore(),
                "hiringScore", score.getHiringScore(),
                "accommodationScore", score.getAccommodationScore(),
                "communityScore", score.getCommunityScore(),
                "totalHires", score.getTotalHires(),
                "accommodationFulfilRate", score.getAccommodationFulfilRate(),
                "inclusivityBadges", badges.stream().map(b -> b.getTier().name()).toList(),
                "communityRatings", Map.of("count", ratingCount, "average", avgRating != null ? avgRating : 0)
        );
    }

    // ── FR6: Award inclusivity badge (Admin only) ─────────────
    @Transactional
    public InclusivityBadge awardBadge(UUID adminId, UUID employerId, BadgeTier tier) {
        User admin    = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        // Deactivate old badges of same tier
        badgeRepo.findByEmployerIdAndIsActiveTrue(employerId)
                .stream().filter(b -> b.getTier() == tier)
                .forEach(b -> { b.setIsActive(false); badgeRepo.save(b); });

        InclusivityBadge badge = InclusivityBadge.builder()
                .employer(employer)
                .tier(tier)
                .awardedBy(admin)
                .validUntil(LocalDateTime.now().plusYears(1))
                .build();

        log.info("Inclusivity badge {} awarded to employer {} by admin {}", tier, employerId, adminId);
        return badgeRepo.save(badge);
    }

    // ── FR6: Submit anonymous employer rating ─────────────────
    @Transactional
    public EmployerRating submitRating(UUID raterId, UUID employerId,
                                       int score, String comment, boolean anonymous) {
        if (score < 1 || score > 5) throw new BadRequestException("Score must be between 1 and 5");

        User rater    = userRepository.findById(raterId)
                .orElseThrow(() -> new ResourceNotFoundException("Rater not found"));
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        EmployerRating rating = EmployerRating.builder()
                .employer(employer).rater(rater)
                .score(score).comment(comment).isAnonymous(anonymous)
                .build();

        EmployerRating saved = ratingRepo.save(rating);
        // Trigger score recompute async
        recomputeScore(employerId);
        return saved;
    }

    // ── FR6: Get paginated ratings ────────────────────────────
    public Page<EmployerRating> getRatings(UUID employerId, Pageable pageable) {
        return ratingRepo.findByEmployerId(employerId, pageable);
    }

    // ── Helpers ───────────────────────────────────────────────
    private WorkplaceRealityScore createDefaultScore(UUID employerId) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return scoreRepo.save(WorkplaceRealityScore.builder().employer(employer).build());
    }
}
