package com.abilitybridge.reporting.entity;

import com.abilitybridge.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "employer_flags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployerFlag {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employer_id") private User employer;
    @Builder.Default @Column(name = "flagged_at") private LocalDateTime flaggedAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "flagged_by") private User flaggedBy;
    private String reason;
    @Builder.Default @Column(name = "is_resolved") private Boolean isResolved = false;
    @Column(name = "resolved_at") private LocalDateTime resolvedAt;
    @Column(name = "admin_notes", columnDefinition = "TEXT") private String adminNotes;
}
