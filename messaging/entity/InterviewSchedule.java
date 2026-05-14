package com.abilitybridge.messaging.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "interview_schedules")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InterviewSchedule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "job_id", nullable = false) private UUID jobId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "applicant_id") private User applicant;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employer_id")  private User employer;
    @Column(name = "scheduled_at", nullable = false) private LocalDateTime scheduledAt;
    @Column(name = "duration_mins") @Builder.Default private Integer durationMins = 60;
    private String format;        // VIDEO | PHONE | IN_PERSON
    @Column(name = "meeting_url") private String meetingUrl;
    @Column(columnDefinition = "TEXT") private String notes;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
