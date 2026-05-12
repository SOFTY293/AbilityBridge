package com.abilitybridge.task.repository;

import com.abilitybridge.task.entity.TaskApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskApplicationRepository extends JpaRepository<TaskApplication, UUID> {
    List<TaskApplication> findByTaskId(UUID taskId);
    Page<TaskApplication> findByApplicantId(UUID applicantId, Pageable pageable);
    Optional<TaskApplication> findByTaskIdAndApplicantId(UUID taskId, UUID applicantId);
    boolean existsByTaskIdAndApplicantId(UUID taskId, UUID applicantId);
}
