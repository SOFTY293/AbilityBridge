package com.abilitybridge.skills.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
@Data
public class SkillAssessmentItem {
    @NotNull private UUID skillId;
    private String proficiency = "BEGINNER";
}
