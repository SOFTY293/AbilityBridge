package com.abilitybridge.task.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_applications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "applicant_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private MicroTask task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(columnDefinition = "TEXT")
    private String coverNote;

    /** PENDING | ACCEPTED | REJECTED */
    @Builder.Default
    private String status = "PENDING";

    @CreatedDate
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;
}
