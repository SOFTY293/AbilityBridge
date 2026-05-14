package com.abilitybridge.messaging.service;

import com.abilitybridge.exception.*;
import com.abilitybridge.messaging.entity.InterviewSchedule;
import com.abilitybridge.messaging.repository.InterviewScheduleRepository;
import com.abilitybridge.notification.entity.NotificationType;
import com.abilitybridge.notification.service.NotificationService;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewScheduleRepository interviewRepo;
    private final UserRepository              userRepository;
    private final NotificationService         notificationService;

    // ── Schedule an interview ─────────────────────────────────
    @Transactional
    public InterviewSchedule scheduleInterview(UUID employerId, UUID applicantId, UUID jobId,
                                               LocalDateTime scheduledAt, Integer durationMins,
                                               String format, String meetingUrl, String notes) {
        User employer  = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));

        if (scheduledAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Interview cannot be scheduled in the past");
        }

        InterviewSchedule interview = InterviewSchedule.builder()
                .jobId(jobId)
                .employer(employer)
                .applicant(applicant)
                .scheduledAt(scheduledAt)
                .durationMins(durationMins != null ? durationMins : 60)
                .format(format)
                .meetingUrl(meetingUrl)
                .notes(notes)
                .build();

        interview = interviewRepo.save(interview);

        // Notify applicant
        notificationService.send(applicant, NotificationType.INTERVIEW_SCHEDULED,
                "Interview Scheduled",
                "Your interview is confirmed for " + scheduledAt.toString(),
                interview.getId());

        log.info("Interview scheduled: employer={} applicant={} at {}", employerId, applicantId, scheduledAt);
        return interview;
    }

    // ── Update interview ──────────────────────────────────────
    @Transactional
    public InterviewSchedule updateInterview(UUID employerId, UUID interviewId,
                                              LocalDateTime newTime, String newMeetingUrl) {
        InterviewSchedule interview = interviewRepo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getEmployer().getId().equals(employerId)) {
            throw new ForbiddenException("You did not schedule this interview");
        }

        if (newTime != null)       interview.setScheduledAt(newTime);
        if (newMeetingUrl != null) interview.setMeetingUrl(newMeetingUrl);

        interview = interviewRepo.save(interview);

        // Notify applicant of rescheduling
        notificationService.send(interview.getApplicant(), NotificationType.INTERVIEW_SCHEDULED,
                "Interview Rescheduled",
                "Your interview has been moved to " + interview.getScheduledAt(),
                interview.getId());

        return interview;
    }

    // ── Get interviews ────────────────────────────────────────
    public List<InterviewSchedule> getMyInterviews(UUID userId, String role) {
        if ("EMPLOYER".equals(role)) return interviewRepo.findByEmployerId(userId);
        return interviewRepo.findByApplicantId(userId);
    }

    public List<InterviewSchedule> getInterviewsForJob(UUID employerId, UUID jobId) {
        return interviewRepo.findByJobId(jobId);
    }
}
