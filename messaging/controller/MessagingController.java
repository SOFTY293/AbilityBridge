package com.abilitybridge.messaging.controller;

import com.abilitybridge.messaging.entity.*;
import com.abilitybridge.messaging.service.MessagingService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Messaging", description = "In-app messaging between seekers, employers and mentors — FR8")
public class MessagingController {

    private final MessagingService messagingService;
    private final JwtUtil          jwtUtil;

    @GetMapping("/conversations")
    @Operation(summary = "Get all my conversations")
    public ResponseEntity<List<Conversation>> myConversations(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(messagingService.getMyConversations(uid(auth)));
    }

    @PostMapping("/conversations")
    @Operation(summary = "Start a new conversation")
    public ResponseEntity<Conversation> startConversation(
            @RequestBody StartConversationRequest req,
            @RequestHeader("Authorization") String auth) {
        Conversation conv = messagingService.getOrCreateConversation(
                uid(auth), req.getRecipientId(),
                req.getType() != null ? req.getType() : ConversationType.DIRECT,
                req.getReferenceId());
        return ResponseEntity.status(HttpStatus.CREATED).body(conv);
    }

    @PostMapping("/conversations/{conversationId}")
    @Operation(summary = "Send a message (text or voice)")
    public ResponseEntity<Message> sendMessage(
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                messagingService.sendMessage(uid(auth), conversationId,
                        req.getContent(), req.getFormat(), req.getMediaUrl()));
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get messages in a conversation (marks as read)")
    public ResponseEntity<Page<Message>> getMessages(
            @PathVariable UUID conversationId,
            @RequestHeader("Authorization") String auth,
            Pageable pageable) {
        return ResponseEntity.ok(messagingService.getMessages(uid(auth), conversationId, pageable));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }

    @Data static class StartConversationRequest {
        private UUID recipientId;
        private ConversationType type;
        private UUID referenceId;
    }
    @Data static class SendMessageRequest {
        private String content;
        private String format;   // TEXT | VOICE | FILE
        private String mediaUrl;
    }
}
