package com.backend.applyingservice.service;

import com.backend.applyingservice.dto.request.ApplyRequest;
import com.backend.applyingservice.dto.response.ApplicationResponse;
import com.backend.applyingservice.dto.response.EmployerDashboardStatsDto;
import com.backend.applyingservice.enums.ApplicationStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.applyingservice.entity.Application;

public interface ApplicationService extends IService<Application> {
    ApplicationResponse applyJob(UUID studentId, ApplyRequest request);
    List<ApplicationResponse> getMyApplications(UUID studentId);
    ApplicationResponse getApplicationDetailForEmployer(UUID employerId, UUID applicationId);
    Page<ApplicationResponse> getApplicationsByPostId(UUID employerId, UUID jobPostId, int page, int size);
    void updateApplicationStatus(UUID employerId, UUID applicationId, ApplicationStatus status, String note);
    EmployerDashboardStatsDto getStatsForEmployer(List<UUID> postIds);
}