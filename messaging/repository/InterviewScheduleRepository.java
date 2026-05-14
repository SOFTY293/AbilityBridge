package com.abilitybridge.messaging.repository;

import com.abilitybridge.messaging.entity.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, UUID> {
    List<InterviewSchedule> findByApplicantId(UUID applicantId);
    List<InterviewSchedule> findByEmployerId(UUID employerId);
    List<InterviewSchedule> findByJobId(UUID jobId);
}
