package com.abilitybridge.task.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_ratings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"task_id","rater_id","ratee_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskRating {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "task_id")
    private MicroTask task;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "rater_id")
    private User rater;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "ratee_id")
    private User ratee;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
