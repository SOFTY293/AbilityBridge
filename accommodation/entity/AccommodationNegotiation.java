package com.abilitybridge.accommodation.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accommodation_negotiations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccommodationNegotiation {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_id")
    private UUID jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeker_id", nullable = false)
    private User seeker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @Enumerated(EnumType.STRING)
    @Builder.Default private NegotiationStatus status = NegotiationStatus.OPEN;

    private BigDecimal compatibility;

    @Column(name = "opened_at")
    @Builder.Default private LocalDateTime openedAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
