package com.abilitybridge.mentorship.repository;
import com.abilitybridge.mentorship.entity.MentorProfile;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, UUID> {
    Optional<MentorProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    @Query("SELECT m FROM MentorProfile m WHERE m.isAvailable = true AND " +
           "(:industry IS NULL OR LOWER(m.industry) LIKE LOWER(CONCAT('%',:industry,'%'))) AND " +
           "(:careerStage IS NULL OR m.careerStage = :careerStage) AND " +
           "(:disabilityType IS NULL OR LOWER(m.disabilityType) LIKE LOWER(CONCAT('%',:disabilityType,'%')))")
    Page<MentorProfile> searchMentors(
            @Param("industry") String industry,
            @Param("careerStage") String careerStage,
            @Param("disabilityType") String disabilityType,
            Pageable pageable);
}
