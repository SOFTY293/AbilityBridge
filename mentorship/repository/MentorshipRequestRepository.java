package com.abilitybridge.mentorship.repository;

import com.abilitybridge.mentorship.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, UUID> {
    Page<MentorshipRequest> findByMentorId(UUID mentorId, Pageable pageable);
    Page<MentorshipRequest> findByMenteeId(UUID menteeId, Pageable pageable);
    Optional<MentorshipRequest> findByMentorIdAndMenteeId(UUID mentorId, UUID menteeId);
    boolean existsByMentorIdAndMenteeId(UUID mentorId, UUID menteeId);
    long countByMentorIdAndStatus(UUID mentorId, MentorshipStatus status);
    long countByStatus(MentorshipStatus status);
}
