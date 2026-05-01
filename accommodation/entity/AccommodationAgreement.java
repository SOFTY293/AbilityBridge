package com.abilitybridge.accommodation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accommodation_agreements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccommodationAgreement {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negotiation_id", nullable = false)
    private AccommodationNegotiation negotiation;

    @Column(name = "agreed_terms", nullable = false)
    private String agreedTerms;

    @Column(name = "agreed_at")
    @Builder.Default private LocalDateTime agreedAt = LocalDateTime.now();

    @Builder.Default @Column(name = "seeker_signed") private Boolean seekerSigned = false;
    @Builder.Default @Column(name = "employer_signed") private Boolean employerSigned = false;
}
