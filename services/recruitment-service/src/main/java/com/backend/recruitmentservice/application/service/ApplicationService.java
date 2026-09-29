package com.backend.recruitmentservice.application.service;

import com.backend.recruitmentservice.application.dto.request.ApplyRequest;
import com.backend.recruitmentservice.application.dto.response.ApplicationResponse;
import com.backend.recruitmentservice.application.dto.response.EmployerDashboardStatsDto;
import com.backend.recruitmentservice.application.enums.ApplicationStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.recruitmentservice.application.entity.Application;

public interface ApplicationService extends IService<Application> {
    ApplicationResponse applyJob(UUID studentId, ApplyRequest request);
    List<ApplicationResponse> getMyApplications(UUID studentId);
    ApplicationResponse getApplicationDetailForEmployer(UUID employerId, UUID applicationId);
    Page<ApplicationResponse> getApplicationsByPostId(UUID employerId, UUID jobPostId, int page, int size);
    void updateApplicationStatus(UUID employerId, UUID applicationId, ApplicationStatus status, String note);
    EmployerDashboardStatsDto getStatsForEmployer(List<UUID> postIds);
}