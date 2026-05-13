package com.abilitybridge.profile.dto;
import lombok.*;
import java.util.UUID;

@Data @Builder
public class EmployerProfileDto {
    private UUID id;
    private UUID userId;
    private String companyName;
    private String industry;
    private String companySize;
    private String website;
    private String description;
    private String location;
    private Boolean isVerified;
}
