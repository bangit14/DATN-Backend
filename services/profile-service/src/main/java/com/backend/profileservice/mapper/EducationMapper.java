package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.candidate.education.EducationCreateRequest;
import com.backend.profileservice.dto.request.candidate.education.EducationUpdateRequest;
import com.backend.profileservice.dto.response.candidate.education.EducationResponse;
import com.backend.profileservice.entity.Education;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEntity(EducationCreateRequest request);

    EducationResponse toResponse(Education entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Education education, EducationUpdateRequest request);
}