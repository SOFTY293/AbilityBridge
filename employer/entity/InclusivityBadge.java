package com.abilitybridge.employer.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "inclusivity_badges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InclusivityBadge {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employer_id") private User employer;
    @Enumerated(EnumType.STRING) private BadgeTier tier;
    @Column(name = "awarded_at") @Builder.Default private LocalDateTime awardedAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "awarded_by") private User awardedBy;
    @Column(name = "valid_until") private LocalDateTime validUntil;
    @Column(name = "is_active") @Builder.Default private Boolean isActive = true;
}
