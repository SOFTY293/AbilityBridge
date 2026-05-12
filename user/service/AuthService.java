package com.abilitybridge.user.service;

import com.abilitybridge.exception.BadRequestException;
import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.exception.UnauthorizedException;
import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.user.dto.AuthDtos.*;
import com.abilitybridge.user.dto.UserSummaryDto;
import com.abilitybridge.user.entity.RefreshToken;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.RefreshTokenRepository;
import com.abilitybridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository         userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;
    private final AuthenticationManager  authManager;
    private final JwtUtil                jwtUtil;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // ── Register ──────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.getEmail() == null && req.getPhone() == null) {
            throw new BadRequestException("Either email or phone must be provided");
        }
        if (req.getEmail() != null && userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (req.getPhone() != null && userRepository.existsByPhone(req.getPhone())) {
            throw new BadRequestException("Phone already registered");
        }

        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} role={}", user.getId(), user.getRole());

        return buildAuthResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────
    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getCredential(), req.getPassword()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmailOrPhone(req.getCredential(), req.getCredential())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // ── Refresh Token ─────────────────────────────────────────
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest req) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getRevoked() || storedToken.isExpired()) {
            throw new UnauthorizedException("Refresh token has expired or been revoked");
        }

        User user = storedToken.getUser();
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken  = generateAccessToken(user);
        String newRefreshToken = generateAndSaveRefreshToken(user);

        return new TokenRefreshResponse(newAccessToken, newRefreshToken);
    }

    // ── Logout ────────────────────────────────────────────────
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }

    // ── Helpers ───────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User user) {
        // Revoke old tokens before issuing new ones
        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken  = generateAccessToken(user);
        String refreshToken = generateAndSaveRefreshToken(user);

        UserSummaryDto summary = UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();

        return new AuthResponse(accessToken, refreshToken, jwtExpirationMs / 1000, summary);
    }

    private String generateAccessToken(User user) {
        org.springframework.security.core.userdetails.User ud =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail() != null ? user.getEmail() : user.getPhone(),
                        "", java.util.List.of());
        return jwtUtil.generateAccessToken(ud, user.getId(), user.getRole().name());
    }

    private String generateAndSaveRefreshToken(User user) {
        String credential = user.getEmail() != null ? user.getEmail() : user.getPhone();
        String rawToken   = jwtUtil.generateRefreshToken(credential);

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .token(rawToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .build();

        refreshTokenRepository.save(entity);
        return rawToken;
    }
}
