package com.abilitybridge.task.service;

import com.abilitybridge.exception.*;
import com.abilitybridge.notification.service.NotificationService;
import com.abilitybridge.task.dto.TaskDtos.*;
import com.abilitybridge.task.entity.*;
import com.abilitybridge.task.repository.*;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final MicroTaskRepository         microTaskRepo;
    private final TaskApplicationRepository   taskApplicationRepo;
    private final TaskCompletionRepository    taskCompletionRepo;
    private final TaskRatingRepository        taskRatingRepo;
    private final UserRepository              userRepository;
    private final NotificationService         notificationService;

    // ── Post task ─────────────────────────────────────────────
    @Transactional
    public MicroTaskDto postTask(UUID posterId, MicroTaskRequest req) {
        User poster = userRepository.findById(posterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MicroTask task = MicroTask.builder()
                .poster(poster)
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .payRate(req.getPayRate())
                .currency(req.getCurrency())
                .requirements(req.getRequirements())
                .deadline(req.getDeadline())
                .status(TaskStatus.OPEN)
                .build();

        task = microTaskRepo.save(task);
        log.info("Micro task posted: {} by {}", task.getId(), posterId);
        return toDto(task);
    }

    // ── Browse tasks ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<MicroTaskDto> browseTasks(String category, Pageable pageable) {
        if (category != null) {
            return microTaskRepo.findByCategoryAndStatus(category, TaskStatus.OPEN, pageable).map(this::toDto);
        }
        return microTaskRepo.findByStatus(TaskStatus.OPEN, pageable).map(this::toDto);
    }

    // ── Apply for task ────────────────────────────────────────
    @Transactional
    public TaskApplicationDto applyForTask(UUID seekerId, UUID taskId, TaskApplicationRequest req) {
        MicroTask task = microTaskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getStatus() != TaskStatus.OPEN) {
            throw new BadRequestException("Task is not open for applications");
        }
        if (task.getPoster().getId().equals(seekerId)) {
            throw new BadRequestException("You cannot apply to your own task");
        }
        if (taskApplicationRepo.existsByTaskIdAndApplicantId(taskId, seekerId)) {
            throw new BadRequestException("You have already applied for this task");
        }

        User seeker = userRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker not found"));

        TaskApplication application = TaskApplication.builder()
                .task(task)
                .applicant(seeker)
                .coverNote(req.getCoverNote())
                .status("ACCEPTED")
                .build();
        application = taskApplicationRepo.save(application);

        task.setStatus(TaskStatus.IN_PROGRESS);
        microTaskRepo.save(task);

        notificationService.notifyTaskAssigned(seeker, task);
        log.info("Task {} assigned to seeker {}", taskId, seekerId);

        return TaskApplicationDto.builder()
                .id(application.getId())
                .taskId(taskId)
                .taskTitle(task.getTitle())
                .applicantId(seekerId)
                .coverNote(req.getCoverNote())
                .status("ACCEPTED")
                .appliedAt(application.getAppliedAt())
                .build();
    }

    // ── Submit task ───────────────────────────────────────────
    @Transactional
    public TaskCompletionDto submitTask(UUID workerId, UUID taskId, TaskSubmissionRequest req) {
        MicroTask task = microTaskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new BadRequestException("Task is not in progress");
        }

        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found"));

        TaskCompletion completion = TaskCompletion.builder()
                .task(task)
                .worker(worker)
                .submissionUrl(req.getSubmissionUrl())
                .submissionNotes(req.getSubmissionNotes())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        task.setStatus(TaskStatus.SUBMITTED);
        microTaskRepo.save(task);
        completion = taskCompletionRepo.save(completion);

        notificationService.notifyTaskSubmitted(task.getPoster(), task);
        return toCompletionDto(completion);
    }

    // ── Approve task & trigger payment ────────────────────────
    @Transactional
    public TaskCompletionDto approveTask(UUID approverId, UUID completionId) {
        TaskCompletion completion = taskCompletionRepo.findById(completionId)
                .orElseThrow(() -> new ResourceNotFoundException("Task completion not found"));

        MicroTask task = completion.getTask();
        if (!task.getPoster().getId().equals(approverId)) {
            throw new ForbiddenException("Only the task poster can approve");
        }

        task.setStatus(TaskStatus.APPROVED);
        microTaskRepo.save(task);

        // Simulate payment trigger (real impl would call PaymentGatewayService)
        completion.setPaymentStatus(PaymentStatus.PROCESSING);
        String paymentRef = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        completion.setPaymentReference(paymentRef);
        completion.setApprovedAt(java.time.LocalDateTime.now());
        completion = taskCompletionRepo.save(completion);

        log.info("Task {} approved, payment ref {}", task.getId(), paymentRef);
        return toCompletionDto(completion);
    }

    // ── Rate task partner ─────────────────────────────────────
    @Transactional
    public TaskRatingDto rateTaskPartner(UUID raterId, UUID taskId, TaskRatingRequest req) {
        MicroTask task = microTaskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getStatus() != TaskStatus.APPROVED) {
            throw new BadRequestException("Task must be approved before rating");
        }
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new ResourceNotFoundException("Rater not found"));
        User ratee = userRepository.findById(req.getRateeId())
                .orElseThrow(() -> new ResourceNotFoundException("Ratee not found"));

        TaskRating rating = TaskRating.builder()
                .task(task)
                .rater(rater)
                .ratee(ratee)
                .score(req.getScore())
                .comment(req.getComment())
                .build();

        rating = taskRatingRepo.save(rating);
        return TaskRatingDto.builder()
                .id(rating.getId())
                .taskId(taskId)
                .raterId(raterId)
                .rateeId(req.getRateeId())
                .score(req.getScore())
                .comment(req.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────
    private MicroTaskDto toDto(MicroTask t) {
        return MicroTaskDto.builder()
                .id(t.getId())
                .posterId(t.getPoster().getId())
                .posterName(t.getPoster().getEmail())
                .title(t.getTitle())
                .description(t.getDescription())
                .category(t.getCategory())
                .payRate(t.getPayRate())
                .currency(t.getCurrency())
                .requirements(t.getRequirements())
                .deadline(t.getDeadline())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private TaskCompletionDto toCompletionDto(TaskCompletion c) {
        return TaskCompletionDto.builder()
                .id(c.getId())
                .taskId(c.getTask().getId())
                .workerId(c.getWorker().getId())
                .submissionUrl(c.getSubmissionUrl())
                .submissionNotes(c.getSubmissionNotes())
                .submittedAt(c.getSubmittedAt())
                .approvedAt(c.getApprovedAt())
                .paymentStatus(c.getPaymentStatus())
                .paymentReference(c.getPaymentReference())
                .build();
    }
}
