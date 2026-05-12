package com.abilitybridge.skills.dto;
import lombok.*; import java.util.UUID;
@Data @Builder
public class CourseDto {
    private UUID id;
    private String title, provider, url;
    private Boolean isFree;
    private Integer durationHours;
}
