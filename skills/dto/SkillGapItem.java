package com.abilitybridge.skills.dto;
import lombok.*; import java.util.List; import java.util.UUID;
@Data @Builder
public class SkillGapItem {
    private UUID skillId;
    private String skillName;
    private List<CourseDto> recommendedCourses;
}
