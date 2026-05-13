package com.abilitybridge.profile.dto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder
public class SeekerProfileDto {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String headline;
    private String bio;
    private String location;
    private String profilePictureUrl;
    private String cvUrl;
    private Boolean anonymousMode;
    private String targetJobCategory;
    private String availability;
    private LocalDateTime updatedAt;
}
