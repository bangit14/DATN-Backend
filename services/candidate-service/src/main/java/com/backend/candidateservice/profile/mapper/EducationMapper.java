package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.candidate.education.EducationCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.education.EducationUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.education.EducationResponse;
import com.backend.candidateservice.profile.entity.Education;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEntity(EducationCreateRequest request);

    EducationResponse toResponse(Education entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Education education, EducationUpdateRequest request);
}