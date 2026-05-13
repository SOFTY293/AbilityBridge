package com.abilitybridge.profile.repository;
import com.abilitybridge.profile.entity.AccessibilitySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessibilitySettingsRepository extends JpaRepository<AccessibilitySettings, UUID> {
    Optional<AccessibilitySettings> findByUserId(UUID userId);
}
