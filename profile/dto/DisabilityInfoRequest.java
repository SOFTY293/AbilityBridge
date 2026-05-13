package com.abilitybridge.profile.dto;
import lombok.Data;

@Data
public class DisabilityInfoRequest {
    private String disabilityType;
    private String disclosureLevel = "PRIVATE";
    private String supportNeeds;
}
