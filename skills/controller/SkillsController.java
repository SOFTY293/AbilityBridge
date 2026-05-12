package com.abilitybridge.skills.controller;

import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.skills.dto.*;
import com.abilitybridge.skills.service.SkillsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Skills & Learning", description = "Self-assessment, gap analysis, courses, badges, portfolio — FR2")
public class SkillsController {

    private final SkillsService skillsService;
    private final JwtUtil       jwtUtil;

    // Assessment
    @PostMapping("/assessment")
    @Operation(summary = "Submit skills self-assessment — FR2")
    public ResponseEntity<List<SeekerSkillDto>> submitAssessment(
            @Valid @RequestBody List<SkillAssessmentItem> items,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(skillsService.saveAssessment(uid(auth), items));
    }

    @GetMapping("/my-skills")
    @Operation(summary = "Get my skills list")
    public ResponseEntity<List<SeekerSkillDto>> getMySkills(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(skillsService.getMySkills(uid(auth)));
    }

    // Gap analysis
    @GetMapping("/gap-analysis")
    @Operation(summary = "Get skill gap report based on target job category — FR2")
    public ResponseEntity<SkillGapReport> getGapAnalysis(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(skillsService.getSkillGapAnalysis(uid(auth)));
    }

    // Badges
    @PostMapping("/badges")
    @Operation(summary = "Award skill badge after completing a linked course — FR2")
    public ResponseEntity<SkillBadgeDto> awardBadge(
            @RequestParam UUID skillId,
            @RequestParam(required = false) UUID courseId,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillsService.awardBadge(uid(auth), skillId, courseId));
    }

    @GetMapping("/badges")
    @Operation(summary = "Get all skill badges I have earned")
    public ResponseEntity<List<SkillBadgeDto>> getMyBadges(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(skillsService.getMyBadges(uid(auth)));
    }

    // Portfolio
    @GetMapping("/portfolio")
    @Operation(summary = "Get my skills portfolio — FR2")
    public ResponseEntity<List<PortfolioItemDto>> getPortfolio(
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(skillsService.getPortfolio(uid(auth)));
    }

    @PostMapping("/portfolio")
    @Operation(summary = "Add an item to my portfolio")
    public ResponseEntity<PortfolioItemDto> addPortfolioItem(
            @Valid @RequestBody PortfolioItemRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillsService.addPortfolioItem(uid(auth), req));
    }

    // Public view of another user's portfolio
    @GetMapping("/portfolio/{userId}")
    @Operation(summary = "View another seekers public portfolio")
    public ResponseEntity<List<PortfolioItemDto>> getPublicPortfolio(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillsService.getPortfolio(userId));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }
}
