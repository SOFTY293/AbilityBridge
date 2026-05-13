package com.abilitybridge.profile.repository;
import com.abilitybridge.profile.entity.SeekerDisabilityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeekerDisabilityInfoRepository extends JpaRepository<SeekerDisabilityInfo, UUID> {
    Optional<SeekerDisabilityInfo> findByUserId(UUID userId);
}
