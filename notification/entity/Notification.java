package com.abilitybridge.notification.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @Enumerated(EnumType.STRING) private NotificationType type;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "TEXT") private String body;
    @Column(name = "reference_id") private UUID referenceId;
    @Builder.Default @Column(name = "is_read") private Boolean isRead = false;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
