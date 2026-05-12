package com.abilitybridge.reporting.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "discrimination_reports")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiscriminationReport {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reporter_id") private User reporter;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employer_id") private User employer;
    @Column(columnDefinition = "TEXT", nullable = false) private String description;
    @Column(name = "evidence_url") private String evidenceUrl;
    @Builder.Default @Column(name = "is_anonymous") private Boolean isAnonymous = true;
    @Enumerated(EnumType.STRING) @Builder.Default private ReportStatus status = ReportStatus.SUBMITTED;
    @CreatedDate @Column(name = "submitted_at", updatable = false) private LocalDateTime submittedAt;
    @Column(name = "reviewed_at")  private LocalDateTime reviewedAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewed_by") private User reviewedBy;
}
