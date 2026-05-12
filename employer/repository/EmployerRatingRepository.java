package com.abilitybridge.employer.repository;
import com.abilitybridge.employer.entity.EmployerRating;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.UUID;
public interface EmployerRatingRepository extends JpaRepository<EmployerRating, UUID> {
    Page<EmployerRating> findByEmployerId(UUID employerId, Pageable pageable);
    @Query("SELECT AVG(r.score) FROM EmployerRating r WHERE r.employer.id = :employerId")
    Double averageScoreByEmployer(UUID employerId);
    long countByEmployerId(UUID employerId);
}
