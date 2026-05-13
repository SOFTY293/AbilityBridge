package com.abilitybridge.profile.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stored separately from public profile data per NFR3 — GDPR data isolation.
 * Disability info is never included in general profile responses.
 */
@Entity
@Table(name = "seeker_disability_info")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeekerDisabilityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String disabilityType;

    /** PRIVATE | EMPLOYERS_ONLY | PUBLIC */
    @Builder.Default
    private String disclosureLevel = "PRIVATE";

    @Column(columnDefinition = "TEXT")
    private String supportNeeds;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
