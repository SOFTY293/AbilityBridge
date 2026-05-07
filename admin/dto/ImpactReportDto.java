package com.abilitybridge.admin.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ImpactReportDto {
    private long totalUsers;
    private long activeSeeker;
    private long activeEmployers;
    private long activeMentors;
    private long totalJobsPosted;
    private long totalJobsFilled;
    private long totalMicroTasksCompleted;
    private long totalMentorshipConnections;
    private long totalDiscriminationReports;
    private long pendingReports;
    private long flaggedEmployers;
    private long badgesAwarded;
    private LocalDateTime generatedAt;
}
