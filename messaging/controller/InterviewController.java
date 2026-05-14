package com.abilitybridge.messaging.controller;

import com.abilitybridge.messaging.entity.InterviewSchedule;
import com.abilitybridge.messaging.service.InterviewService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Interview Scheduling", description = "Schedule and manage interviews — FR8")
public class InterviewController {

    private final InterviewService interviewService;
    private final JwtUtil          jwtUtil;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Employer schedules an interview with an applicant")
    public ResponseEntity<InterviewSchedule> schedule(
            @RequestBody ScheduleRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                interviewService.scheduleInterview(
                        uid(auth), req.getApplicantId(), req.getJobId(),
                        req.getScheduledAt(), req.getDurationMins(),
                        req.getFormat(), req.getMeetingUrl(), req.getNotes()));
    }

    @PatchMapping("/{interviewId}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Reschedule or update meeting link")
    public ResponseEntity<InterviewSchedule> update(
            @PathVariable UUID interviewId,
            @RequestBody UpdateRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(
                interviewService.updateInterview(uid(auth), interviewId,
                        req.getScheduledAt(), req.getMeetingUrl()));
    }

    @GetMapping
    @Operation(summary = "Get my upcoming interviews")
    public ResponseEntity<List<InterviewSchedule>> myInterviews(
            @RequestParam(defaultValue = "SEEKER") String role,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(interviewService.getMyInterviews(uid(auth), role));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @Operation(summary = "Get all interviews for a specific job listing")
    public ResponseEntity<List<InterviewSchedule>> byJob(
            @PathVariable UUID jobId,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(interviewService.getInterviewsForJob(uid(auth), jobId));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    @Data static class ScheduleRequest {
        private UUID applicantId;
        private UUID jobId;
        private LocalDateTime scheduledAt;
        private Integer durationMins;
        private String format;
        private String meetingUrl;
        private String notes;
    }
    @Data static class UpdateRequest {
        private LocalDateTime scheduledAt;
        private String meetingUrl;
    }
}
