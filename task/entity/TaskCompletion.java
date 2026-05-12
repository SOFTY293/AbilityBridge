package com.abilitybridge.task.entity;
import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "task_completions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskCompletion {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "task_id")
    private MicroTask task;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "worker_id")
    private User worker;
    private String submissionUrl;
    @Column(columnDefinition = "TEXT")
    private String submissionNotes;
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();
    private LocalDateTime approvedAt;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String paymentReference;
}
