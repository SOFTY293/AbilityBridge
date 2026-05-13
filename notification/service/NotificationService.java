package com.abilitybridge.notification.service;

import com.abilitybridge.job.entity.JobApplication;
import com.abilitybridge.job.entity.JobListing;
import com.abilitybridge.notification.entity.*;
import com.abilitybridge.notification.repository.NotificationRepository;
import com.abilitybridge.task.entity.MicroTask;
import com.abilitybridge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepo;

    // ── Core send ─────────────────────────────────────────────
    @Async
    @Transactional
    public void send(User recipient, NotificationType type, String title, String body, UUID referenceId) {
        Notification notification = Notification.builder()
                .user(recipient).type(type).title(title).body(body).referenceId(referenceId)
                .build();
        notificationRepo.save(notification);
        log.debug("Notification sent to {} — {}", recipient.getId(), title);
    }

    // ── Domain-specific helpers ───────────────────────────────
    public void notifyApplicationReceived(User employer, JobListing job, UUID applicationId) {
        send(employer, NotificationType.APPLICATION_UPDATE,
                "New Application Received",
                "Someone applied to: " + job.getTitle(),
                applicationId);
    }

    public void notifyApplicationStatusChanged(User seeker, JobApplication application) {
        send(seeker, NotificationType.APPLICATION_UPDATE,
                "Application Status Updated",
                "Your application for '" + application.getJob().getTitle()
                        + "' is now: " + application.getStatus().name(),
                application.getId());
    }

    public void notifyTaskAssigned(User worker, MicroTask task) {
        send(worker, NotificationType.TASK_ASSIGNED,
                "Task Assigned to You",
                "You have been assigned: " + task.getTitle(),
                task.getId());
    }

    public void notifyTaskSubmitted(User poster, MicroTask task) {
        send(poster, NotificationType.TASK_SUBMITTED,
                "Task Submitted for Review",
                "A submission is ready for: " + task.getTitle(),
                task.getId());
    }

    public void notifyMessageReceived(User recipient, UUID conversationId, String senderName) {
        send(recipient, NotificationType.MESSAGE_RECEIVED,
                "New Message from " + senderName,
                "You have a new message",
                conversationId);
    }

    public void notifyBadgeAwarded(User employer, String badgeTier) {
        send(employer, NotificationType.BADGE_AWARDED,
                "Inclusivity Badge Awarded!",
                "Congratulations — you've earned the " + badgeTier + " Inclusivity Badge",
                employer.getId());
    }

    // ── Query ─────────────────────────────────────────────────
    public Page<Notification> getMyNotifications(UUID userId, Pageable pageable) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long countUnread(UUID userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public int markAllRead(UUID userId) {
        return notificationRepo.markAllReadByUserId(userId);
    }
}
