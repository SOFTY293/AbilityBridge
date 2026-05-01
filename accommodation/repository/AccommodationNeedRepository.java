package com.abilitybridge.accommodation.repository;
import com.abilitybridge.accommodation.entity.AccommodationNeed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AccommodationNeedRepository extends JpaRepository<AccommodationNeed, UUID> {
    List<AccommodationNeed> findByUserId(UUID userId);
    List<AccommodationNeed> findByUserIdAndIsMandatoryTrue(UUID userId);
}
