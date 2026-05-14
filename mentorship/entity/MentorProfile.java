package com.abilitybridge.mentorship.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "mentor_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorProfile {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) private User user;
    private String industry;
    @Column(name = "disability_type") private String disabilityType;
    @Column(name = "career_stage")    private String careerStage;
    @Column(columnDefinition = "TEXT") private String bio;
    @Column(name = "offers_cv_review")   @Builder.Default private Boolean offersCvReview  = false;
    @Column(name = "offers_interview")   @Builder.Default private Boolean offersInterview  = false;
    @Column(name = "offers_career_adv")  @Builder.Default private Boolean offersCareerAdv  = false;
    @Column(name = "max_mentees")        @Builder.Default private Integer maxMentees        = 3;
    @Column(name = "is_available")       @Builder.Default private Boolean isAvailable       = true;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
