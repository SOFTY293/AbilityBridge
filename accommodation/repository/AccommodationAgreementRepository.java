package com.abilitybridge.accommodation.repository;
import com.abilitybridge.accommodation.entity.AccommodationAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AccommodationAgreementRepository extends JpaRepository<AccommodationAgreement, UUID> {
    List<AccommodationAgreement> findByNegotiationId(UUID negotiationId);
}
