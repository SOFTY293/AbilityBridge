package com.abilitybridge.profile.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployerProfileRequest {
    @NotBlank private String companyName;
    private String industry;
    private String companySize;
    private String website;
    private String description;
    private String location;
}
