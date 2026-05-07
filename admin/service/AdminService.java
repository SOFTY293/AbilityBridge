package com.abilitybridge.admin.service;

import com.abilitybridge.admin.dto.ImpactReportDto;
import com.abilitybridge.admin.dto.UserAdminDto;
import com.abilitybridge.employer.repository.InclusivityBadgeRepository;
import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.job.repository.JobApplicationRepository;
import com.abilitybridge.job.repository.JobListingRepository;
import com.abilitybridge.mentorship.repository.MentorshipRequestRepository;
import com.abilitybridge.profile.entity.EmployerProfile;
import com.abilitybridge.profile.repository.EmployerProfileRepository;
import com.abilitybridge.reporting.entity.ReportStatus;
import com.abilitybridge.reporting.repository.DiscriminationReportRepository;
import com.abilitybridge.reporting.repository.EmployerFlagRepository;
import com.abilitybridge.task.entity.TaskStatus;
import com.abilitybridge.task.repository.MicroTaskRepository;
import com.abilitybridge.user.entity.*;
import com.abilitybridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository                 userRepository;
    private final EmployerProfileRepository      employerProfileRepo;
    private final JobListingRepository           jobListingRepo;
    private final JobApplicationRepository       jobApplicationRepo;
    private final MicroTaskRepository            microTaskRepo;
    private final MentorshipRequestRepository    mentorshipRequestRepo;
    private final DiscriminationReportRepository reportRepo;
    private final EmployerFlagRepository         flagRepo;
    private final InclusivityBadgeRepository     badgeRepo;

    // ── FR10: Platform-wide analytics dashboard ───────────────
    public ImpactReportDto generateImpactReport() {
        return ImpactReportDto.builder()
                .totalUsers(userRepository.count())
                .activeSeeker(userRepository.countActiveByRole(UserRole.SEEKER))
                .activeEmployers(userRepository.countActiveByRole(UserRole.EMPLOYER))
                .activeMentors(userRepository.countActiveByRole(UserRole.MENTOR))
                .totalJobsPosted(jobListingRepo.count())
                .totalJobsFilled(jobApplicationRepo.countByStatus(
                        com.abilitybridge.job.entity.ApplicationStatus.OFFERED))
                .totalMicroTasksCompleted(microTaskRepo.countByStatus(TaskStatus.APPROVED))
                .totalMentorshipConnections(mentorshipRequestRepo.countByStatus(
                        com.abilitybridge.mentorship.entity.MentorshipStatus.ACTIVE))
                .totalDiscriminationReports(reportRepo.count())
                .pendingReports(reportRepo.countByStatus(ReportStatus.SUBMITTED))
                .flaggedEmployers(flagRepo.countByIsResolvedFalse())
                .badgesAwarded(badgeRepo.countByIsActiveTrue())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    // ── FR10: User management ─────────────────────────────────
    public Page<UserAdminDto> listUsers(UserRole role, AccountStatus status, Pageable pageable) {
        Page<User> users;
        if (role != null && status != null) {
            users = userRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            users = userRepository.findByRole(role, pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(status, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::toAdminDto);
    }

    // ── FR10: Verify / suspend / remove user ──────────────────
    @Transactional
    public UserAdminDto updateUserStatus(UUID adminId, UUID targetUserId, AccountStatus newStatus) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AccountStatus old = target.getStatus();
        target.setStatus(newStatus);
        userRepository.save(target);
        log.info("Admin {} changed user {} status {} → {}", adminId, targetUserId, old, newStatus);
        return toAdminDto(target);
    }

    // ── FR10: Verify employer profile ─────────────────────────
    @Transactional
    public EmployerProfile verifyEmployer(UUID adminId, UUID employerUserId) {
        EmployerProfile profile = employerProfileRepo.findByUserId(employerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

        profile.setIsVerified(true);
        profile.setVerifiedAt(LocalDateTime.now());

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        profile.setVerifiedBy(admin);

        log.info("Admin {} verified employer {}", adminId, employerUserId);
        return employerProfileRepo.save(profile);
    }

    // ── FR10: Get user detail ─────────────────────────────────
    public UserAdminDto getUserDetail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toAdminDto(user);
    }

    // ── Helpers ───────────────────────────────────────────────
    private UserAdminDto toAdminDto(User u) {
        return UserAdminDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .status(u.getStatus())
                .emailVerified(u.getEmailVerified())
                .createdAt(u.getCreatedAt())
                .lastLoginAt(u.getLastLoginAt())
                .build();
    }
}
