package com.abilitybridge.task.repository;

import com.abilitybridge.task.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MicroTaskRepository extends JpaRepository<MicroTask, UUID> {
    Page<MicroTask> findByStatus(TaskStatus status, Pageable pageable);
    Page<MicroTask> findByCategoryAndStatus(String category, TaskStatus status, Pageable pageable);
    Page<MicroTask> findByPosterId(UUID posterId, Pageable pageable);
    long countByStatus(TaskStatus status);
}
