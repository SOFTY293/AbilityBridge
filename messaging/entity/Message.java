package com.abilitybridge.messaging.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "conversation_id") private Conversation conversation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sender_id") private User sender;
    @Column(columnDefinition = "TEXT") private String content;
    @Column(name = "message_format") @Builder.Default private String messageFormat = "TEXT";
    @Column(name = "media_url") private String mediaUrl;
    @Builder.Default @Column(name = "is_read") private Boolean isRead = false;
    @CreatedDate @Column(name = "sent_at", updatable = false) private LocalDateTime sentAt;
}
