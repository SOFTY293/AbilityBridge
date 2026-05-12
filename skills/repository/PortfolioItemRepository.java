package com.abilitybridge.skills.repository;
import com.abilitybridge.skills.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, UUID> {
    List<PortfolioItem> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
