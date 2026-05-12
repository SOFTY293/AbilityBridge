package com.abilitybridge.task.entity;
import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "micro_tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MicroTask {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "poster_id")
    private User poster;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    private String category;
    @Column(nullable = false)
    private BigDecimal payRate;
    @Builder.Default private String currency = "USD";
    @Column(columnDefinition = "TEXT")
    private String requirements;
    private LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.OPEN;
    @CreatedDate @Column(updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}
