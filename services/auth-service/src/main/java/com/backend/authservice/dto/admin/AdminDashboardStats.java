package com.backend.authservice.dto.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStats {
    private long totalUsers;
    private long totalCandidates;
    private long totalEmployers;
    private long activeUsers;
    private long bannedUsers;
    private long totalJobs;
    private long pendingJobs;
}
