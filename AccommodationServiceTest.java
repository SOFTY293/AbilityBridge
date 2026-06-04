package com.abilitybridge.accommodation.service;

import com.abilitybridge.accommodation.entity.*;
import com.abilitybridge.accommodation.repository.*;
import com.abilitybridge.exception.ForbiddenException;
import com.abilitybridge.exception.ResourceNotFoundException;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock AccommodationNeedRepository       needRepo;
    @Mock AccommodationNegotiationRepository negotiationRepo;
    @Mock AccommodationAgreementRepository  agreementRepo;
    @Mock UserRepository                    userRepository;

    @InjectMocks AccommodationService accommodationService;

    private User seeker;
    private User employer;
    private UUID seekerId;
    private UUID employerId;

    @BeforeEach
    void setUp() {
        seekerId   = UUID.randomUUID();
        employerId = UUID.randomUUID();
        seeker     = User.builder().id(seekerId).email("seeker@test.com").build();
        employer   = User.builder().id(employerId).email("employer@test.com").build();
    }

    @Test
    @DisplayName("addNeed: creates and returns accommodation need")
    void addNeed_success() {
        when(userRepository.findById(seekerId)).thenReturn(Optional.of(seeker));

        AccommodationNeed saved = AccommodationNeed.builder()
                .id(UUID.randomUUID()).user(seeker)
                .needType("Screen Reader Support").isMandatory(true).build();

        when(needRepo.save(any())).thenReturn(saved);

        AccommodationNeed result = accommodationService.addNeed(
                seekerId, "Screen Reader Support", null, true);

        assertThat(result.getNeedType()).isEqualTo("Screen Reader Support");
        assertThat(result.getIsMandatory()).isTrue();
    }

    @Test
    @DisplayName("checkCompatibility: returns 100 when seeker has no mandatory needs")
    void checkCompatibility_noNeeds_returns100() {
        when(needRepo.findByUserIdAndIsMandatoryTrue(seekerId)).thenReturn(List.of());

        BigDecimal score = accommodationService.checkCompatibility(seekerId, employerId, null);

        assertThat(score).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("openNegotiation: creates negotiation channel with OPEN status")
    void openNegotiation_success() {
        when(userRepository.findById(seekerId)).thenReturn(Optional.of(seeker));
        when(userRepository.findById(employerId)).thenReturn(Optional.of(employer));
        when(needRepo.findByUserIdAndIsMandatoryTrue(seekerId)).thenReturn(List.of());

        AccommodationNegotiation saved = AccommodationNegotiation.builder()
                .id(UUID.randomUUID()).seeker(seeker).employer(employer)
                .status(NegotiationStatus.OPEN).compatibility(BigDecimal.valueOf(100)).build();

        when(negotiationRepo.save(any())).thenReturn(saved);

        AccommodationNegotiation result = accommodationService.openNegotiation(
                seekerId, employerId, UUID.randomUUID());

        assertThat(result.getStatus()).isEqualTo(NegotiationStatus.OPEN);
        assertThat(result.getSeeker().getId()).isEqualTo(seekerId);
    }

    @Test
    @DisplayName("logAgreement: throws ForbiddenException for non-participant")
    void logAgreement_forbiddenForNonParticipant() {
        UUID strangerUserId    = UUID.randomUUID();
        UUID negotiationId     = UUID.randomUUID();

        AccommodationNegotiation negotiation = AccommodationNegotiation.builder()
                .id(negotiationId).seeker(seeker).employer(employer)
                .status(NegotiationStatus.OPEN).build();

        when(negotiationRepo.findById(negotiationId)).thenReturn(Optional.of(negotiation));

        assertThatThrownBy(() ->
                accommodationService.logAgreement(strangerUserId, negotiationId, "some terms"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("logAgreement: sets negotiation status to AGREED")
    void logAgreement_setsAgreedStatus() {
        UUID negotiationId = UUID.randomUUID();
        AccommodationNegotiation negotiation = AccommodationNegotiation.builder()
                .id(negotiationId).seeker(seeker).employer(employer)
                .status(NegotiationStatus.OPEN).build();

        when(negotiationRepo.findById(negotiationId)).thenReturn(Optional.of(negotiation));
        when(negotiationRepo.save(any())).thenReturn(negotiation);

        AccommodationAgreement agreement = AccommodationAgreement.builder()
                .id(UUID.randomUUID()).negotiation(negotiation)
                .agreedTerms("Flexible hours provided").seekerSigned(true).build();

        when(agreementRepo.save(any())).thenReturn(agreement);

        AccommodationAgreement result = accommodationService.logAgreement(
                seekerId, negotiationId, "Flexible hours provided");

        assertThat(negotiation.getStatus()).isEqualTo(NegotiationStatus.AGREED);
        assertThat(result.getAgreedTerms()).isEqualTo("Flexible hours provided");
    }
}
