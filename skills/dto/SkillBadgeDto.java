package com.abilitybridge.skills.dto;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder
public class SkillBadgeDto {
    private UUID id, skillId;
    private String skillName;
    private LocalDateTime awardedAt;
}
