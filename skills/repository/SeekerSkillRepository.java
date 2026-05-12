package com.abilitybridge.skills.repository;
import com.abilitybridge.skills.entity.SeekerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SeekerSkillRepository extends JpaRepository<SeekerSkill, UUID> {
    List<SeekerSkill> findByUserId(UUID userId);
    boolean existsByUserIdAndSkillId(UUID userId, UUID skillId);

    @Query("SELECT ss.skill.id FROM SeekerSkill ss WHERE ss.user.id = :userId")
    Set<UUID> findSkillIdsByUserId(UUID userId);
}
