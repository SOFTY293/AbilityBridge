package com.abilitybridge.profile.dto;
import lombok.Data;

@Data
public class AccessibilitySettingsDto {
    private Boolean screenReader  = false;
    private Boolean highContrast  = false;
    private Boolean voiceNav      = false;
    private String  fontSize      = "MEDIUM";
    private Boolean dyslexiaFont  = false;
    private Boolean signLangVideo = false;
}
