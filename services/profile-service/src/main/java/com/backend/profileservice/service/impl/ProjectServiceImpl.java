package com.backend.profileservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.profileservice.dto.request.candidate.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.candidate.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.candidate.project.ProjectResponse;
import com.backend.profileservice.entity.Candidate;
import com.backend.profileservice.entity.Project;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.ProjectMapper;
import com.backend.profileservice.mapper.db.CandidateDbMapper;
import com.backend.profileservice.mapper.db.ProjectDbMapper;
import com.backend.profileservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl extends ServiceImpl<ProjectDbMapper, Project> implements ProjectService {

    private final ProjectDbMapper projectDbMapper;
    private final ProjectMapper projectMapper;
    private final CandidateDbMapper candidateDbMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ProjectResponse create(UUID userId, ProjectCreateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Project project = projectMapper.toEntity(request);
        if (project.getId() == null) {
            project.setId(UUID.randomUUID());
        }
        if (project.getCreatedAt() == null) {
            project.setCreatedAt(Instant.now());
        }
        if (project.getUpdatedAt() == null) {
            project.setUpdatedAt(Instant.now());
        }
        project.setCandidateId(candidate.getId());

        projectDbMapper.insert(project);
        return projectMapper.toResponse(project);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ProjectResponse update(UUID userId, UUID projectId, ProjectUpdateRequest request) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Project project = projectDbMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getId, projectId)
                        .eq(Project::getCandidateId, candidate.getId())
        );
        if (project == null) {
            throw new AppException(ErrorCode.PROJECT_NOT_FOUND);
        }

        projectMapper.updateEntity(project, request);
        projectDbMapper.updateById(project);

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<ProjectResponse> getAllByCandidate(UUID userId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            return List.of();
        }

        List<Project> list = projectDbMapper.selectList(
                new LambdaQueryWrapper<Project>().eq(Project::getCandidateId, candidate.getId())
        );

        return list.stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void delete(UUID userId, UUID projectId) {
        Candidate candidate = candidateDbMapper.selectOne(
                new LambdaQueryWrapper<Candidate>().eq(Candidate::getUserId, userId)
        );
        if (candidate == null) {
            throw new AppException(ErrorCode.CANDIDATE_NOT_FOUND);
        }

        Project project = projectDbMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getId, projectId)
                        .eq(Project::getCandidateId, candidate.getId())
        );
        if (project == null) {
            throw new AppException(ErrorCode.PROJECT_NOT_FOUND);
        }

        projectDbMapper.deleteById(projectId);
    }
}
