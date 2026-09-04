package com.backend.profileservice.service;

import com.backend.profileservice.dto.request.candidate.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.candidate.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.candidate.project.ProjectResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.Project;

public interface ProjectService extends IService<Project> {
    ProjectResponse create(UUID userId, ProjectCreateRequest request);

    ProjectResponse update(UUID userId, UUID projectId, ProjectUpdateRequest request);

    void delete(UUID userId, UUID projectId);

    List<ProjectResponse> getAllByCandidate(UUID userId);
}
