package com.abilitybridge.skills.entity;
import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "seeker_skills",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","skill_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeekerSkill {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Builder.Default
    private String proficiency = "BEGINNER";
    @Builder.Default
    private Boolean verified = false;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
