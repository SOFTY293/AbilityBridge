package com.abilitybridge.profile.repository;

import com.abilitybridge.profile.entity.SeekerProfile;
import com.abilitybridge.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, UUID> {
    Optional<SeekerProfile> findByUser(User user);
    Optional<SeekerProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
