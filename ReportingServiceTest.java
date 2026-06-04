package com.abilitybridge.reporting.service;

import com.abilitybridge.exception.BadRequestException;
import com.abilitybridge.reporting.entity.*;
import com.abilitybridge.reporting.repository.*;
import com.abilitybridge.user.entity.*;
import com.abilitybridge.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock DiscriminationReportRepository reportRepo;
    @Mock EmployerFlagRepository         flagRepo;
    @Mock UserRepository                 userRepository;

    @InjectMocks ReportingService reportingService;

    private User reporter;
    private User employer;
    private UUID reporterId;
    private UUID employerId;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();
        employerId = UUID.randomUUID();
        reporter   = User.builder().id(reporterId).email("reporter@test.com").build();
        employer   = User.builder().id(employerId).email("employer@test.com").build();
    }

    @Test
    @DisplayName("fileReport: saves report and returns it")
    void fileReport_success() {
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(employerId)).thenReturn(Optional.of(employer));

        DiscriminationReport saved = DiscriminationReport.builder()
                .id(UUID.randomUUID())
                .reporter(reporter)
                .employer(employer)
                .description("Discriminatory job posting")
                .isAnonymous(true)
                .status(ReportStatus.SUBMITTED)
                .build();

        when(reportRepo.save(any())).thenReturn(saved);
        when(reportRepo.countByEmployerIdAndStatus(employerId, ReportStatus.SUBMITTED))
                .thenReturn(1L); // below threshold

        DiscriminationReport result = reportingService.fileReport(
                reporterId, employerId, "Discriminatory job posting", null, true);

        assertThat(result.getDescription()).isEqualTo("Discriminatory job posting");
        assertThat(result.getIsAnonymous()).isTrue();
        assertThat(result.getStatus()).isEqualTo(ReportStatus.SUBMITTED);
        verify(reportRepo).save(any());
    }

    @Test
    @DisplayName("fileReport: auto-flags employer when threshold exceeded")
    void fileReport_autoFlagsEmployer() {
        when(userRepository.findById(reporterId)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(employerId)).thenReturn(Optional.of(employer));

        DiscriminationReport saved = DiscriminationReport.builder()
                .id(UUID.randomUUID()).reporter(reporter).employer(employer)
                .description("Hostile environment").isAnonymous(true)
                .status(ReportStatus.SUBMITTED).build();

        when(reportRepo.save(any())).thenReturn(saved);
        // 3 reports = at threshold → triggers auto-flag
        when(reportRepo.countByEmployerIdAndStatus(employerId, ReportStatus.SUBMITTED))
                .thenReturn(3L);
        when(flagRepo.countByEmployerIdAndIsResolvedFalse(employerId)).thenReturn(0L);
        when(flagRepo.save(any())).thenReturn(null);

        reportingService.fileReport(reporterId, employerId, "Hostile environment", null, true);

        verify(flagRepo).save(any()); // flag was created
    }

    @Test
    @DisplayName("takeActionOnEmployer: suspends employer")
    void takeAction_suspends() {
        UUID adminId = UUID.randomUUID();
        User admin = User.builder().id(adminId).email("admin@test.com").build();
        employer.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(employerId)).thenReturn(Optional.of(employer));
        when(userRepository.save(any())).thenReturn(employer);
        when(flagRepo.findByEmployerIdAndIsResolvedFalse(employerId)).thenReturn(List.of());

        reportingService.takeActionOnEmployer(adminId, employerId, AccountStatus.SUSPENDED, "Repeated violations");

        assertThat(employer.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        verify(userRepository).save(employer);
    }

    @Test
    @DisplayName("takeActionOnEmployer: throws BadRequest for invalid action")
    void takeAction_invalidAction() {
        assertThatThrownBy(() ->
                reportingService.takeActionOnEmployer(UUID.randomUUID(), employerId,
                        AccountStatus.ACTIVE, "notes"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("SUSPENDED or REMOVED");
    }
}
