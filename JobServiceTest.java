package com.abilitybridge.job.service;

import com.abilitybridge.exception.BadRequestException;
import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.job.dto.JobDtos.*;
import com.abilitybridge.job.entity.*;
import com.abilitybridge.job.repository.*;
import com.abilitybridge.notification.service.NotificationService;
import com.abilitybridge.profile.repository.EmployerProfileRepository;
import com.abilitybridge.skills.repository.SeekerSkillRepository;
import com.abilitybridge.skills.repository.SkillRepository;
import com.abilitybridge.user.entity.*;
import com.abilitybridge.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock JobListingRepository     jobListingRepo;
    @Mock JobApplicationRepository jobApplicationRepo;
    @Mock UserRepository           userRepository;
    @Mock EmployerProfileRepository employerProfileRepo;
    @Mock SkillRepository          skillRepository;
    @Mock SeekerSkillRepository    seekerSkillRepo;
    @Mock NotificationService      notificationService;

    @InjectMocks JobService jobService;

    private User employer;
    private User seeker;
    private UUID employerId;
    private UUID seekerId;

    @BeforeEach
    void setUp() {
        employerId = UUID.randomUUID();
        seekerId   = UUID.randomUUID();
        employer   = User.builder().id(employerId).email("employer@test.com")
                        .role(UserRole.EMPLOYER).build();
        seeker     = User.builder().id(seekerId).email("seeker@test.com")
                        .role(UserRole.SEEKER).build();
    }

    @Test
    @DisplayName("postJob: creates and returns job listing")
    void postJob_success() {
        JobListingRequest req = new JobListingRequest();
        req.setTitle("Backend Developer");
        req.setDescription("Spring Boot role");
        req.setJobType(JobType.FULL_TIME);

        when(userRepository.findById(employerId)).thenReturn(Optional.of(employer));

        JobListing saved = JobListing.builder()
                .id(UUID.randomUUID())
                .employer(employer)
                .title("Backend Developer")
                .jobType(JobType.FULL_TIME)
                .status(JobStatus.ACTIVE)
                .requiredSkills(new HashSet<>())
                .build();

        when(jobListingRepo.save(any())).thenReturn(saved);
        when(employerProfileRepo.findByUserId(employerId)).thenReturn(Optional.empty());

        JobListingDto result = jobService.postJob(employerId, req);

        assertThat(result.getTitle()).isEqualTo("Backend Developer");
        assertThat(result.getStatus()).isEqualTo(JobStatus.ACTIVE);
    }

    @Test
    @DisplayName("postJob: throws ResourceNotFoundException when employer not found")
    void postJob_employerNotFound() {
        when(userRepository.findById(employerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.postJob(employerId, new JobListingRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("applyToJob: throws BadRequest when already applied")
    void applyToJob_alreadyApplied() {
        UUID jobId = UUID.randomUUID();
        JobListing job = JobListing.builder()
                .id(jobId).employer(employer)
                .status(JobStatus.ACTIVE)
                .requiredSkills(new HashSet<>())
                .build();

        when(userRepository.findById(seekerId)).thenReturn(Optional.of(seeker));
        when(jobListingRepo.findById(jobId)).thenReturn(Optional.of(job));
        when(jobApplicationRepo.existsByJobIdAndApplicantId(jobId, seekerId)).thenReturn(true);

        JobApplicationRequest req = new JobApplicationRequest();
        req.setApplyFormat(ApplyFormat.CV);

        assertThatThrownBy(() -> jobService.applyToJob(seekerId, jobId, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already applied");
    }

    @Test
    @DisplayName("applyToJob: throws BadRequest when job is closed")
    void applyToJob_jobClosed() {
        UUID jobId = UUID.randomUUID();
        JobListing job = JobListing.builder()
                .id(jobId).employer(employer)
                .status(JobStatus.CLOSED)
                .requiredSkills(new HashSet<>())
                .build();

        when(userRepository.findById(seekerId)).thenReturn(Optional.of(seeker));
        when(jobListingRepo.findById(jobId)).thenReturn(Optional.of(job));
        when(jobApplicationRepo.existsByJobIdAndApplicantId(jobId, seekerId)).thenReturn(false);

        JobApplicationRequest req = new JobApplicationRequest();
        req.setApplyFormat(ApplyFormat.CV);

        assertThatThrownBy(() -> jobService.applyToJob(seekerId, jobId, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not accepting applications");
    }

    @Test
    @DisplayName("computeMatchScore: returns 100 when no required skills")
    void computeMatchScore_noSkillsRequired() {
        JobListing job = JobListing.builder()
                .requiredSkills(new HashSet<>())
                .build();

        when(seekerSkillRepo.findSkillIdsByUserId(seekerId)).thenReturn(new HashSet<>());

        Double score = jobService.computeMatchScore(seekerId, job);
        assertThat(score).isEqualTo(100.0);
    }

    @Test
    @DisplayName("computeMatchScore: returns partial score for partial skill match")
    void computeMatchScore_partialMatch() {
        var s1 = new com.abilitybridge.skills.entity.Skill();
        s1.setId(UUID.randomUUID());
        var s2 = new com.abilitybridge.skills.entity.Skill();
        s2.setId(UUID.randomUUID());

        JobListing job = JobListing.builder()
                .requiredSkills(new HashSet<>(Set.of(s1, s2)))
                .build();

        // Seeker only has s1
        when(seekerSkillRepo.findSkillIdsByUserId(seekerId))
                .thenReturn(new HashSet<>(Set.of(s1.getId())));

        Double score = jobService.computeMatchScore(seekerId, job);
        assertThat(score).isEqualTo(50.0);
    }
}
