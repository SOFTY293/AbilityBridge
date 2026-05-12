package com.abilitybridge.skills.dto;
import lombok.*; import java.util.UUID;
@Data @Builder
public class SeekerSkillDto {
    private UUID id, skillId;
    private String skillName, category, proficiency;
    private Boolean verified;
}
