package com.abilitybridge.messaging.repository;
import com.abilitybridge.messaging.entity.Message;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByConversationIdOrderBySentAtDesc(UUID conversationId, Pageable pageable);
    long countByConversationIdAndIsReadFalseAndSenderIdNot(UUID conversationId, UUID userId);
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :convId AND m.sender.id != :userId")
    void markConversationRead(UUID convId, UUID userId);
}
