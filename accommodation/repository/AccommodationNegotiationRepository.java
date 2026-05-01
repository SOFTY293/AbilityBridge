package com.abilitybridge.accommodation.repository;
import com.abilitybridge.accommodation.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AccommodationNegotiationRepository extends JpaRepository<AccommodationNegotiation, UUID> {
    List<AccommodationNegotiation> findBySeekerId(UUID seekerId);
    List<AccommodationNegotiation> findByEmployerId(UUID employerId);
    List<AccommodationNegotiation> findBySeekerIdAndEmployerId(UUID seekerId, UUID employerId);
}
