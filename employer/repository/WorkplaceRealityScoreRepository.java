package com.abilitybridge.employer.repository;
import com.abilitybridge.employer.entity.WorkplaceRealityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface WorkplaceRealityScoreRepository extends JpaRepository<WorkplaceRealityScore, UUID> {
    Optional<WorkplaceRealityScore> findByEmployerId(UUID employerId);
}
