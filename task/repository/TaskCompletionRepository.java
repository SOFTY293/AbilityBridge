package com.abilitybridge.task.repository;
import com.abilitybridge.task.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, UUID> {
    Optional<TaskCompletion> findByTaskId(UUID taskId);
    long countByWorkerIdAndPaymentStatus(UUID workerId, PaymentStatus status);
}
