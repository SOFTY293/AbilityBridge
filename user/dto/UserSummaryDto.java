package com.abilitybridge.user.dto;

import com.abilitybridge.user.entity.AccountStatus;
import com.abilitybridge.user.entity.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSummaryDto {
    private UUID id;
    private String email;
    private String phone;
    private UserRole role;
    private AccountStatus status;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
}
