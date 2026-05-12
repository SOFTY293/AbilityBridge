package com.abilitybridge.task.dto;

import com.abilitybridge.task.entity.PaymentStatus;
import com.abilitybridge.task.entity.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskDtos {

    @Data
    public static class MicroTaskRequest {
        @NotBlank private String title;
        @NotBlank private String description;
        private String category;
        @NotNull @DecimalMin("0.01") private BigDecimal payRate;
        private String currency = "USD";
        private String requirements;
        private LocalDateTime deadline;
    }

    @Data @Builder
    public static class MicroTaskDto {
        private UUID id;
        private UUID posterId;
        private String posterName;
        private String title;
        private String description;
        private String category;
        private BigDecimal payRate;
        private String currency;
        private String requirements;
        private LocalDateTime deadline;
        private TaskStatus status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class TaskApplicationRequest {
        private String coverNote;
    }

    @Data @Builder
    public static class TaskApplicationDto {
        private UUID id;
        private UUID taskId;
        private String taskTitle;
        private UUID applicantId;
        private String coverNote;
        private String status;
        private LocalDateTime appliedAt;
    }

    @Data
    public static class TaskSubmissionRequest {
        private String submissionUrl;
        private String submissionNotes;
    }

    @Data @Builder
    public static class TaskCompletionDto {
        private UUID id;
        private UUID taskId;
        private UUID workerId;
        private String submissionUrl;
        private String submissionNotes;
        private LocalDateTime submittedAt;
        private LocalDateTime approvedAt;
        private PaymentStatus paymentStatus;
        private String paymentReference;
    }

    @Data
    public static class TaskRatingRequest {
        @NotNull private UUID rateeId;
        @Min(1) @Max(5) private int score;
        private String comment;
    }

    @Data @Builder
    public static class TaskRatingDto {
        private UUID id;
        private UUID taskId;
        private UUID raterId;
        private UUID rateeId;
        private int score;
        private String comment;
        private LocalDateTime createdAt;
    }
}
