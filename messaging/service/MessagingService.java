package com.abilitybridge.messaging.service;

import com.abilitybridge.exception.*;
import com.abilitybridge.messaging.entity.*;
import com.abilitybridge.messaging.repository.*;
import com.abilitybridge.notification.service.NotificationService;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository      messageRepo;
    private final UserRepository         userRepository;
    private final NotificationService    notificationService;

    // ── Start or get conversation ─────────────────────────────
    @Transactional
    public Conversation getOrCreateConversation(UUID initiatorId, UUID recipientId,
                                                ConversationType type, UUID referenceId) {
        // Look for existing conversation between these two users
        List<Conversation> existing = conversationRepo.findConversationsByParticipant(initiatorId);
        for (Conversation c : existing) {
            if (c.getType() == type
                    && (referenceId == null || referenceId.equals(c.getReferenceId()))) {
                boolean hasRecipient = c.getMessages().stream()
                        .anyMatch(m -> m.getSender().getId().equals(recipientId));
                if (hasRecipient) return c;
            }
        }

        Conversation conversation = Conversation.builder()
                .type(type).referenceId(referenceId).build();
        return conversationRepo.save(conversation);
    }

    // ── Send a message ────────────────────────────────────────
    @Transactional
    public Message sendMessage(UUID senderId, UUID conversationId,
                               String content, String format, String mediaUrl) {
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .messageFormat(format != null ? format : "TEXT")
                .mediaUrl(mediaUrl)
                .build();

        Message saved = messageRepo.save(message);

        // Notify other participants
        conversation.getMessages().stream()
                .map(m -> m.getSender())
                .filter(u -> !u.getId().equals(senderId))
                .distinct()
                .forEach(recipient -> notificationService.notifyMessageReceived(
                        recipient, conversationId, sender.getEmail()));

        return saved;
    }

    // ── Get messages in a conversation ────────────────────────
    @Transactional
    public Page<Message> getMessages(UUID userId, UUID conversationId, Pageable pageable) {
        messageRepo.markConversationRead(conversationId, userId);
        return messageRepo.findByConversationIdOrderBySentAtDesc(conversationId, pageable);
    }

    // ── Get my conversations ──────────────────────────────────
    public List<Conversation> getMyConversations(UUID userId) {
        return conversationRepo.findConversationsByParticipant(userId);
    }
}
