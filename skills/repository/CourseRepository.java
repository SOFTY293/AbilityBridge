package com.abilitybridge.skills.repository;
import com.abilitybridge.skills.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findBySkillIdAndIsFree(UUID skillId, Boolean isFree);
    List<Course> findBySkillId(UUID skillId);
}
