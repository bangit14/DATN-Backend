package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.candidate.project.ProjectCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.project.ProjectUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.project.ProjectResponse;
import com.backend.candidateservice.profile.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "name", source = "projectName")
    Project toEntity(ProjectCreateRequest request);

    @Mapping(target = "projectName", source = "name")
    ProjectResponse toResponse(Project entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", source = "projectName")
    void updateEntity(@MappingTarget Project project, ProjectUpdateRequest request);
}
