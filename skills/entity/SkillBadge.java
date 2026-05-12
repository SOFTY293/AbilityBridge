package com.abilitybridge.skills.entity;
import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "skill_badges",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","skill_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkillBadge {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "skill_id")
    private Skill skill;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id")
    private Course course;
    @Builder.Default
    private LocalDateTime awardedAt = LocalDateTime.now();
}
