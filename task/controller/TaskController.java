package com.abilitybridge.task.controller;

import com.abilitybridge.security.JwtUtil;
import com.abilitybridge.task.dto.TaskDtos.*;
import com.abilitybridge.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Micro Task Marketplace", description = "Post, browse, complete, pay tasks — FR3")
public class TaskController {

    private final TaskService taskService;
    private final JwtUtil     jwtUtil;

    @GetMapping
    @Operation(summary = "Browse open micro tasks")
    public ResponseEntity<Page<MicroTaskDto>> browse(
            @RequestParam(required = false) String category, Pageable pageable) {
        return ResponseEntity.ok(taskService.browseTasks(category, pageable));
    }

    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Post a new micro task")
    public ResponseEntity<MicroTaskDto> post(
            @Valid @RequestBody MicroTaskRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.postTask(userId(auth), req));
    }

    @PostMapping("/{taskId}/apply")
    @PreAuthorize("hasRole('SEEKER')")
    @Operation(summary = "Apply for a micro task")
    public ResponseEntity<TaskApplicationDto> apply(
            @PathVariable UUID taskId,
            @RequestBody TaskApplicationRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(taskService.applyForTask(userId(auth), taskId, req));
    }

    @PostMapping("/{taskId}/submit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit completed task work")
    public ResponseEntity<TaskCompletionDto> submit(
            @PathVariable UUID taskId,
            @RequestBody TaskSubmissionRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(taskService.submitTask(userId(auth), taskId, req));
    }

    @PostMapping("/completions/{completionId}/approve")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Approve task and trigger payment")
    public ResponseEntity<TaskCompletionDto> approve(
            @PathVariable UUID completionId,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.ok(taskService.approveTask(userId(auth), completionId));
    }

    @PostMapping("/{taskId}/rate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Rate task partner after completion")
    public ResponseEntity<TaskRatingDto> rate(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRatingRequest req,
            @RequestHeader("Authorization") String auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.rateTaskPartner(userId(auth), taskId, req));
    }

    private UUID userId(String auth) {
        return jwtUtil.extractUserId(auth.substring(7));
    }
}
