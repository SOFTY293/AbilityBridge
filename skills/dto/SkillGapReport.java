package com.abilitybridge.skills.dto;
import lombok.*; import java.util.List;
@Data @Builder
public class SkillGapReport {
    private String targetCategory;
    private Integer totalRequired, totalMissing, completionPercent;
    private String message;
    private List<SkillGapItem> gaps;
}
