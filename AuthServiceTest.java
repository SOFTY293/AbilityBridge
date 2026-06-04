package com.abilitybridge.user.service;

import com.abilitybridge.exception.BadRequestException;
import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.user.dto.AuthDtos.*;
import com.abilitybridge.user.entity.*;
import com.abilitybridge.user.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository         userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder        passwordEncoder;
    @Mock AuthenticationManager  authManager;
    @Mock JwtUtil                jwtUtil;

    @InjectMocks AuthService authService;

    @BeforeEach
    void setup() {
        org.springframework.test.util.ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000L);
        org.springframework.test.util.ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);
    }

    @Test
    @DisplayName("Register: success creates and returns tokens")
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("seeker@test.com");
        req.setPassword("Password@123");
        req.setRole(UserRole.SEEKER);

        when(userRepository.existsByEmail("seeker@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        User saved = User.builder().id(java.util.UUID.randomUUID())
                .email("seeker@test.com").role(UserRole.SEEKER)
                .status(AccountStatus.PENDING_VERIFICATION).build();
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtUtil.generateAccessToken(any(), any(), any())).thenReturn("access.token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh.token");
        when(refreshTokenRepository.save(any())).thenReturn(null);
        doNothing().when(refreshTokenRepository).revokeAllUserTokens(any());

        AuthResponse resp = authService.register(req);

        assertThat(resp.getAccessToken()).isEqualTo("access.token");
        assertThat(resp.getUser().getEmail()).isEqualTo("seeker@test.com");
    }

    @Test
    @DisplayName("Register: duplicate email throws BadRequestException")
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("taken@test.com");
        req.setPassword("Password@123");
        req.setRole(UserRole.SEEKER);

        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Register: no email or phone throws BadRequestException")
    void register_noContact_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setPassword("Password@123");
        req.setRole(UserRole.SEEKER);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Refresh: revoked token throws UnauthorizedException")
    void refresh_revokedToken_throws() {
        TokenRefreshRequest req = new TokenRefreshRequest();
        req.setRefreshToken("revoked.token");

        RefreshToken revoked = RefreshToken.builder()
                .token("revoked.token")
                .revoked(true)
                .expiresAt(java.time.LocalDateTime.now().plusDays(1))
                .user(User.builder().build())
                .build();
        when(refreshTokenRepository.findByToken("revoked.token")).thenReturn(Optional.of(revoked));

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(com.abilitybridge.exception.UnauthorizedException.class);
    }
}
