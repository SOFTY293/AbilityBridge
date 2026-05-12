package com.abilitybridge.task.repository;

import com.abilitybridge.task.entity.TaskRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TaskRatingRepository extends JpaRepository<TaskRating, UUID> {
    Page<TaskRating> findByRateeId(UUID rateeId, Pageable pageable);

    @Query("SELECT AVG(r.score) FROM TaskRating r WHERE r.ratee.id = :rateeId")
    Double averageScoreByRatee(UUID rateeId);

    boolean existsByTaskIdAndRaterId(UUID taskId, UUID raterId);
}
