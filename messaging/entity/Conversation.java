package com.abilitybridge.messaging.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "conversations")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Enumerated(EnumType.STRING) @Builder.Default private ConversationType type = ConversationType.DIRECT;
    @Column(name = "reference_id") private UUID referenceId;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @Builder.Default private List<Message> messages = new ArrayList<>();
}
