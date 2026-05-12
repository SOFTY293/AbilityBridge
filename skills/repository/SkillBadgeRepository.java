package com.abilitybridge.skills.repository;

import com.abilitybridge.skills.entity.SkillBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SkillBadgeRepository extends JpaRepository<SkillBadge, UUID> {
    @Query("SELECT b FROM SkillBadge b WHERE b.user.id = :userId")
    List<SkillBadge> findByUserId(UUID userId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM SkillBadge b WHERE b.user.id = :userId AND b.skill.id = :skillId")
    boolean existsByUserIdAndSkillId(UUID userId, UUID skillId);
}
