package com.abilitybridge.mentorship.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "mentorship_requests",
    uniqueConstraints = @UniqueConstraint(columnNames = {"mentor_id","mentee_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorshipRequest {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "mentor_id") private User mentor;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "mentee_id") private User mentee;
    @Column(columnDefinition = "TEXT") private String message;
    @Enumerated(EnumType.STRING) @Builder.Default private MentorshipStatus status = MentorshipStatus.PENDING;
    @CreatedDate @Column(name = "requested_at", updatable = false) private LocalDateTime requestedAt;
    @Column(name = "responded_at") private LocalDateTime respondedAt;
}
