package com.abilitybridge.user.dto;

import com.abilitybridge.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ── Register ──────────────────────────────────────────────────
public class AuthDtos {

    @Data
    public static class RegisterRequest {
        @Email(message = "Invalid email format")
        private String email;

        private String phone;

        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotNull
        private UserRole role;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String credential; // email or phone

        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private UserSummaryDto user;

        public AuthResponse(String accessToken, String refreshToken,
                            Long expiresIn, UserSummaryDto user) {
            this.accessToken  = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn    = expiresIn;
            this.user         = user;
        }
    }

    @Data
    public static class TokenRefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Data
    public static class TokenRefreshResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";

        public TokenRefreshResponse(String accessToken, String refreshToken) {
            this.accessToken  = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
