package com.abilitybridge.profile.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeekerProfileRequest {
    @NotBlank private String fullName;
    private String headline;
    private String bio;
    private String location;
    private String targetJobCategory;
    private String availability;
}
