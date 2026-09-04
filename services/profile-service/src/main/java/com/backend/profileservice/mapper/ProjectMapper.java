package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.candidate.project.ProjectCreateRequest;
import com.backend.profileservice.dto.request.candidate.project.ProjectUpdateRequest;
import com.backend.profileservice.dto.response.candidate.project.ProjectResponse;
import com.backend.profileservice.entity.Project;
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
