package com.backend.jobservice.service;

import com.backend.jobservice.dto.request.JobPostPageRequest;
import com.backend.jobservice.dto.request.JobPostRequest;
import com.backend.jobservice.dto.request.JobPostUpdateRequest;
import com.backend.jobservice.dto.response.JobPostResponse;
import com.backend.jobservice.dto.response.JobPostSummaryResponse;
import com.backend.jobservice.dto.response.ListDataRes;
import com.backend.jobservice.entity.JobPost;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface JobPostService extends IService<JobPost> {

    JobPostResponse createPost(UUID employerUserId, JobPostRequest request);

    JobPostResponse updatePost(UUID employerUserId, UUID postId, JobPostUpdateRequest request);

    void hidePost(UUID employerUserId, UUID postId);

    JobPostResponse getPostDetail(UUID postId);

    ListDataRes<JobPostSummaryResponse> getJobPostPage(JobPostPageRequest request);

    Page<JobPostSummaryResponse> searchPosts(String keyword, String workMode, UUID skillId, UUID companyId, String location, String level, Pageable pageable);

    JobPostResponse approvePost(UUID postId, UUID adminId);

    void rejectPost(UUID postId);

    List<JobPostSummaryResponse> getPendingPosts();

    Page<JobPostResponse> getMyPosts(UUID employerUserId, int page, int size);

    JobPostResponse getEmployerPostDetail(UUID employerUserId, UUID postId);

    JobPostResponse getPostDetailForAdmin(UUID id);

    List<JobPostSummaryResponse> getRejectedAndHiddenPosts();

    Map<String, Object> getAdminStats();
}
