package com.backend.recruitmentservice.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.recruitmentservice.application.client.CvClient;
import com.backend.recruitmentservice.application.client.JobClient;
import com.backend.recruitmentservice.application.client.ProfileClient;
import com.backend.recruitmentservice.application.dto.external.InternshipPostResponse;
import com.backend.recruitmentservice.application.dto.external.StudentResponse;
import com.backend.recruitmentservice.application.dto.request.ApplyRequest;
import com.backend.recruitmentservice.application.dto.response.ApplicationResponse;
import com.backend.recruitmentservice.application.dto.response.EmployerDashboardStatsDto;
import com.backend.recruitmentservice.application.entity.Application;
import com.backend.recruitmentservice.application.enums.ApplicationStatus;
import com.backend.recruitmentservice.application.enums.ErrorCode;
import com.backend.recruitmentservice.application.exception.AppException;
import com.backend.recruitmentservice.application.mapper.db.ApplicationDbMapper;
import com.backend.recruitmentservice.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl extends ServiceImpl<ApplicationDbMapper, Application> implements ApplicationService {

    private final ApplicationDbMapper applicationDbMapper;
    private final CvClient cvClient;
    private final JobClient jobClient;
    private final ProfileClient profileClient;

    @Override
    public EmployerDashboardStatsDto getStatsForEmployer(List<UUID> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new EmployerDashboardStatsDto(0, 0);
        }

        Long total = applicationDbMapper.selectCount(
                new LambdaQueryWrapper<Application>().in(Application::getJobPostId, postIds)
        );

        Long newApps = applicationDbMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .in(Application::getJobPostId, postIds)
                        .eq(Application::getStatus, ApplicationStatus.SUBMITTED)
        );

        return new EmployerDashboardStatsDto(total != null ? total : 0, newApps != null ? newApps : 0);
    }

    @Override
    @Transactional
    public ApplicationResponse applyJob(UUID studentId, ApplyRequest request) {
        // 1. Check duplicate apply
        Long count = applicationDbMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .eq(Application::getJobPostId, request.getJobPostId())
        );
        if (count != null && count > 0) {
            throw new AppException(ErrorCode.DUPLICATE_APPLICATION);
        }

        // 2. Validate Job Post (Gọi Job Service)
        var jobApiResponse = jobClient.getPostDetail(request.getJobPostId());
        if (jobApiResponse == null || !jobApiResponse.isSuccess() || jobApiResponse.getData() == null) {
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND, "Job post not found");
        }

        InternshipPostResponse jobData = jobApiResponse.getData();

        if (!"ACTIVE".equalsIgnoreCase(jobData.getStatus())) {
            throw new AppException(ErrorCode.JOB_POST_NOT_ACTIVE);
        }

        // 3. Validate CV (Gọi CV Service)
        try {
            var cvData = cvClient.getCvById(request.getCvId(), studentId.toString(), "STUDENT");
            if (cvData == null) throw new AppException(ErrorCode.CV_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error verifying CV ownership: {}", e.getMessage());
            throw new AppException(ErrorCode.CV_NOT_FOUND, "CV not found or access denied");
        }

        // 4. Save Application
        Application app = Application.builder()
                .jobPostId(request.getJobPostId())
                .employerId(jobData.getPostedBy())
                .studentId(studentId)
                .cvId(request.getCvId())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.SUBMITTED)
                .appliedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        applicationDbMapper.insert(app);

        // 5. Build Response
        return mapToResponse(app, jobData.getTitle(), null);
    }

    @Override
    @Transactional
    public ApplicationResponse getApplicationDetailForEmployer(UUID employerId, UUID applicationId) {
        Application app = applicationDbMapper.selectById(applicationId);
        if (app == null) {
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND);
        }

        // Validate Ownership (Chỉ Employer sở hữu bài đăng mới được xem)
        if (!app.getEmployerId().equals(employerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // AUTO-VIEWED LOGIC: Chuyển status sang VIEWED nếu đang là SUBMITTED
        if (app.getStatus() == ApplicationStatus.SUBMITTED) {
            app.setStatus(ApplicationStatus.VIEWED);
            app.setViewedAt(Instant.now());
            app.setUpdatedAt(Instant.now());
            applicationDbMapper.updateById(app);
        }

        // Fetch data enrichment (Student Profile & Job Title)
        String studentName = "Unknown";
        String studentAvatar = null;
        String jobTitle = "Unknown Job";

        // Lấy thông tin Student
        try {
            var profileRes = profileClient.getStudentFullName(app.getStudentId());

            if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                studentName = profileRes.getData();
            }
        } catch (Exception e) {
            log.warn("Could not fetch student name for app {}: {}", applicationId, e.getMessage());
        }

        // Lấy thông tin Job (để hiển thị title)
        try {
            var jobRes = jobClient.getPostDetail(app.getJobPostId());
            if (jobRes != null && jobRes.isSuccess() && jobRes.getData() != null) {
                jobTitle = jobRes.getData().getTitle();
            }
        } catch (Exception ignored) {
        }

        String cvUrl = null;
        try {
            var cvRes = cvClient.getCvById(
                    app.getCvId(),
                    app.getStudentId().toString(),
                    "STUDENT"
            );
            if (cvRes != null) {
                cvUrl = cvRes.getCvUrl();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch CV URL for app {}: {}", applicationId, e.getMessage());
        }

        // Map response
        ApplicationResponse response = mapToResponse(app, jobTitle, null);
        response.setStudentName(studentName);
        response.setStudentAvatar(studentAvatar);
        response.setCvUrl(cvUrl);

        return response;
    }

    @Override
    public List<ApplicationResponse> getMyApplications(UUID studentId) {
        // Lấy list từ DB
        List<Application> apps = applicationDbMapper.selectList(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .orderByDesc(Application::getAppliedAt)
        );

        // Map sang response (Gọi Job Service để lấy Title)
        return apps.stream().map(app -> {
            String jobTitle = "Unknown Job";
            String companyName = "Unknown Company";

            try {
                var jobRes = jobClient.getPostDetail(app.getJobPostId());
                if (jobRes != null && jobRes.isSuccess() && jobRes.getData() != null) {
                    jobTitle = jobRes.getData().getTitle();
                    companyName = "Company ID: " + jobRes.getData().getCompanyId();
                }
            } catch (Exception ignored) {
            }

            return mapToResponse(app, jobTitle, companyName);
        }).collect(Collectors.toList());
    }

    // --- Helper Mapper ---
    private ApplicationResponse mapToResponse(Application app, String jobTitle, String companyName) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobPostId(app.getJobPostId())
                .jobTitle(jobTitle)
                .companyName(companyName)
                .studentId(app.getStudentId())
                .cvId(app.getCvId())
                .coverLetter(app.getCoverLetter())
                .status(app.getStatus())
                .note(app.getNote())
                .appliedAt(app.getAppliedAt())
                .viewedAt(app.getViewedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    @Override
    public Page<ApplicationResponse> getApplicationsByPostId(UUID employerId, UUID jobPostId, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Application> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<Application>()
                .eq(Application::getJobPostId, jobPostId)
                .eq(Application::getEmployerId, employerId)
                .orderByDesc(Application::getAppliedAt);

        applicationDbMapper.selectPage(mpPage, wrapper);

        if (mpPage.getRecords().isEmpty()) {
            return Page.empty();
        }

        String jobTitle = "Unknown Job";
        try {
            var jobRes = jobClient.getPostDetail(jobPostId);
            if (jobRes != null && jobRes.getData() != null) jobTitle = jobRes.getData().getTitle();
        } catch (Exception ignored) {
        }
        final String finalJobTitle = jobTitle;

        List<UUID> studentIds = mpPage.getRecords().stream()
                .map(Application::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, StudentResponse> tempMap = new HashMap<>();

        if (!studentIds.isEmpty()) {
            try {
                var profileRes = profileClient.getStudentsBatch(studentIds);
                if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                    tempMap = profileRes.getData().stream()
                            .collect(Collectors.toMap(
                                    StudentResponse::getUserId,
                                    Function.identity(),
                                    (existing, replacement) -> existing
                            ));
                }
            } catch (Exception e) {
                log.error("Failed to batch fetch profiles", e);
            }
        }

        final Map<UUID, StudentResponse> studentMap = tempMap;

        List<ApplicationResponse> responses = mpPage.getRecords().stream().map(app -> {
            StudentResponse studentInfo = studentMap.get(app.getStudentId());

            String studentName = (studentInfo != null) ? studentInfo.getFullName() : "Unknown Candidate";
            String studentAvatar = (studentInfo != null) ? studentInfo.getAvatarUrl() : null;

            ApplicationResponse res = mapToResponse(app, finalJobTitle, null);
            res.setStudentName(studentName);
            res.setStudentAvatar(studentAvatar);

            return res;
        }).collect(Collectors.toList());

        return new PageImpl<>(responses, PageRequest.of(page, size), mpPage.getTotal());
    }

    @Override
    @Transactional
    public void updateApplicationStatus(UUID employerId, UUID applicationId, ApplicationStatus status, String note) {
        Application app = applicationDbMapper.selectById(applicationId);
        if (app == null) {
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND);
        }

        if (!app.getEmployerId().equals(employerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        app.setStatus(status);
        app.setNote(note);
        app.setUpdatedAt(Instant.now());

        applicationDbMapper.updateById(app);
    }
}