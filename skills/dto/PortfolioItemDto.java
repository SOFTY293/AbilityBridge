package com.abilitybridge.skills.dto;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder
public class PortfolioItemDto {
    private UUID id;
    private String title, description, itemType, url;
    private LocalDateTime createdAt;
}
