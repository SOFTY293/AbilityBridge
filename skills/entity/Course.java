package com.abilitybridge.skills.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String provider;
    @Column(nullable = false)
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;
    @Builder.Default
    private Boolean isFree = true;
    private Integer durationHours;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
