package com.abilitybridge.notification.controller;

import com.abilitybridge.notification.entity.Notification;
import com.abilitybridge.notification.service.NotificationService;
import com.abilitybridge.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Notifications", description = "User notification feed — FR8")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil             jwtUtil;

    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestHeader("Authorization") String auth, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMyNotifications(uid(auth), pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(Map.of("unread", notificationService.countUnread(uid(auth))));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Map<String, Integer>> markAllRead(@RequestHeader("Authorization") String auth) {
        int count = notificationService.markAllRead(uid(auth));
        return ResponseEntity.ok(Map.of("markedRead", count));
    }

    private UUID uid(String auth) { return jwtUtil.extractUserId(auth.substring(7)); }
}
