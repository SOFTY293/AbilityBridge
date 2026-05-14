package com.abilitybridge.messaging.repository;
import com.abilitybridge.messaging.entity.Conversation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT DISTINCT c FROM Conversation c JOIN c.messages m WHERE m.sender.id = :userId")
    List<Conversation> findConversationsByParticipant(UUID userId);
}
