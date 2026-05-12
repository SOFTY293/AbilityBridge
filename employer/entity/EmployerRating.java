package com.abilitybridge.employer.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "employer_ratings")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployerRating {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employer_id") private User employer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "rater_id")   private User rater;
    private Integer score;
    private String comment;
    @Column(name = "is_anonymous") @Builder.Default private Boolean isAnonymous = true;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
