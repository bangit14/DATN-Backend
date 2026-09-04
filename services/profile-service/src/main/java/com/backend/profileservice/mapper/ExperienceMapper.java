package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.candidate.experience.ExperienceCreateRequest;
import com.backend.profileservice.dto.request.candidate.experience.ExperienceUpdateRequest;
import com.backend.profileservice.dto.response.candidate.experience.ExperienceResponse;
import com.backend.profileservice.entity.Experience;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    @Mapping(target = "isCurrent", source = "current")
    Experience toEntity(ExperienceCreateRequest request);

    @Mapping(target = "current", source = "isCurrent")
    ExperienceResponse toResponse(Experience entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isCurrent", source = "current")
    void updateEntity(@MappingTarget Experience experience, ExperienceUpdateRequest request);
}
