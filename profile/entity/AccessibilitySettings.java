package com.abilitybridge.profile.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accessibility_settings")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessibilitySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    private Boolean screenReader = false;

    @Builder.Default
    private Boolean highContrast = false;

    @Builder.Default
    private Boolean voiceNav = false;

    @Builder.Default
    private String fontSize = "MEDIUM";

    @Builder.Default
    private Boolean dyslexiaFont = false;

    @Builder.Default
    private Boolean signLangVideo = false;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
