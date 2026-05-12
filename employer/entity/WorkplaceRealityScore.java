package com.abilitybridge.employer.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workplace_reality_scores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkplaceRealityScore {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", unique = true) private User employer;
    @Column(name = "overall_score")     @Builder.Default private BigDecimal overallScore   = BigDecimal.ZERO;
    @Column(name = "hiring_score")      @Builder.Default private BigDecimal hiringScore    = BigDecimal.ZERO;
    @Column(name = "accommodation_score") @Builder.Default private BigDecimal accommodationScore = BigDecimal.ZERO;
    @Column(name = "community_score")   @Builder.Default private BigDecimal communityScore = BigDecimal.ZERO;
    @Column(name = "total_hires")       @Builder.Default private Integer totalHires        = 0;
    @Column(name = "accommodation_fulfil_rate") @Builder.Default private BigDecimal accommodationFulfilRate = BigDecimal.ZERO;
    @Column(name = "computed_at")       @Builder.Default private LocalDateTime computedAt  = LocalDateTime.now();
}
