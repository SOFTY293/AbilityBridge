package com.abilitybridge.skills.service;

import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.profile.repository.SeekerProfileRepository;
import com.abilitybridge.skills.dto.*;
import com.abilitybridge.skills.entity.*;
import com.abilitybridge.skills.repository.*;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillsService {

    private final SkillRepository        skillRepo;
    private final SeekerSkillRepository  seekerSkillRepo;
    private final CourseRepository       courseRepo;
    private final SkillBadgeRepository   badgeRepo;
    private final PortfolioItemRepository portfolioRepo;
    private final UserRepository         userRepo;
    private final SeekerProfileRepository profileRepo;

    // ── Skills Self-Assessment ────────────────────────────────
    @Transactional
    public List<SeekerSkillDto> saveAssessment(UUID userId, List<SkillAssessmentItem> items) {
        User user = getUser(userId);
        List<SeekerSkill> saved = new ArrayList<>();

        for (SkillAssessmentItem item : items) {
            Skill skill = skillRepo.findById(item.getSkillId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill", item.getSkillId()));

            SeekerSkill ss = seekerSkillRepo.findByUserId(userId).stream()
                    .filter(s -> s.getSkill().getId().equals(item.getSkillId()))
                    .findFirst()
                    .orElse(SeekerSkill.builder().user(user).skill(skill).build());

            ss.setProficiency(item.getProficiency());
            saved.add(seekerSkillRepo.save(ss));
        }
        return saved.stream().map(this::toSkillDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SeekerSkillDto> getMySkills(UUID userId) {
        return seekerSkillRepo.findByUserId(userId).stream()
                .map(this::toSkillDto).collect(Collectors.toList());
    }

    // ── Skill Gap Analysis ────────────────────────────────────
    @Transactional(readOnly = true)
    public SkillGapReport getSkillGapAnalysis(UUID userId) {
        var profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile", userId));

        String category = profile.getTargetJobCategory();
        if (category == null) {
            return SkillGapReport.builder()
                    .message("Set your target job category in your profile to get a gap analysis")
                    .gaps(List.of())
                    .build();
        }

        // FIXED: Explicitly convert Set to List to resolve type incompatibility
        List<UUID> userSkillIds = new ArrayList<>(seekerSkillRepo.findSkillIdsByUserId(userId));
        List<Skill> categorySkills = skillRepo.findByCategory(category);

        List<Skill> gaps = categorySkills.stream()
                .filter(s -> !userSkillIds.contains(s.getId()))
                .collect(Collectors.toList());

        List<SkillGapItem> gapItems = gaps.stream().map(skill -> {
            List<Course> courses = courseRepo.findBySkillIdAndIsFree(skill.getId(), true);
            return SkillGapItem.builder()
                    .skillId(skill.getId())
                    .skillName(skill.getName())
                    .recommendedCourses(courses.stream().map(this::toCourseDto).collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());

        return SkillGapReport.builder()
                .targetCategory(category)
                .totalRequired(categorySkills.size())
                .totalMissing(gaps.size())
                .completionPercent(categorySkills.isEmpty() ? 100 :
                        (int) (((double)(categorySkills.size() - gaps.size()) / categorySkills.size()) * 100))
                .gaps(gapItems)
                .build();
    }

    // ── Badges ────────────────────────────────────────────────
    @Transactional
    public SkillBadgeDto awardBadge(UUID userId, UUID skillId, UUID courseId) {
        User user = getUser(userId);
        Skill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", skillId));

        if (badgeRepo.existsByUserIdAndSkillId(userId, skillId)) {
            return toBadgeDto(badgeRepo.findByUserId(userId).stream()
                    .filter(b -> b.getSkill().getId().equals(skillId)).findFirst().get());
        }

        Course course = courseId != null ? courseRepo.findById(courseId).orElse(null) : null;
        SkillBadge badge = SkillBadge.builder().user(user).skill(skill).course(course).build();
        badge = badgeRepo.save(badge);

        // Auto-add to portfolio
        PortfolioItem item = PortfolioItem.builder()
                .user(user)
                .title(skill.getName() + " Badge")
                .description("Earned via course completion")
                .itemType("BADGE")
                .referenceId(badge.getId())
                .build();
        portfolioRepo.save(item);

        return toBadgeDto(badge);
    }

    @Transactional(readOnly = true)
    public List<SkillBadgeDto> getMyBadges(UUID userId) {
        return badgeRepo.findByUserId(userId).stream()
                .map(this::toBadgeDto).collect(Collectors.toList());
    }

    // ── Portfolio ─────────────────────────────────────────────
    @Transactional
    public PortfolioItemDto addPortfolioItem(UUID userId, PortfolioItemRequest req) {
        User user = getUser(userId);
        PortfolioItem item = PortfolioItem.builder()
                .user(user)
                .title(req.getTitle())
                .description(req.getDescription())
                .itemType(req.getItemType())
                .url(req.getUrl())
                .build();
        return toPortfolioDto(portfolioRepo.save(item));
    }

    @Transactional(readOnly = true)
    public List<PortfolioItemDto> getPortfolio(UUID userId) {
        return portfolioRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toPortfolioDto).collect(Collectors.toList());
    }

    // ── Mappers ───────────────────────────────────────────────
    private SeekerSkillDto toSkillDto(SeekerSkill ss) {
        return SeekerSkillDto.builder()
                .id(ss.getId())
                .skillId(ss.getSkill().getId())
                .skillName(ss.getSkill().getName())
                .category(ss.getSkill().getCategory())
                .proficiency(ss.getProficiency())
                .verified(ss.getVerified())
                .build();
    }

    private CourseDto toCourseDto(Course c) {
        return CourseDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .provider(c.getProvider())
                .url(c.getUrl())
                .isFree(c.getIsFree())
                .durationHours(c.getDurationHours())
                .build();
    }

    private SkillBadgeDto toBadgeDto(SkillBadge b) {
        return SkillBadgeDto.builder()
                .id(b.getId())
                .skillId(b.getSkill().getId())
                .skillName(b.getSkill().getName())
                .awardedAt(b.getAwardedAt())
                .build();
    }

    private PortfolioItemDto toPortfolioDto(PortfolioItem p) {
        return PortfolioItemDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .itemType(p.getItemType())
                .url(p.getUrl())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private User getUser(UUID userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}