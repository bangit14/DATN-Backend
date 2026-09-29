package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.EmployerUpdateRequest;
import com.backend.candidateservice.profile.dto.response.EmployerResponse;
import com.backend.candidateservice.profile.entity.Employer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface EmployerMapper {

    EmployerResponse toResponse(Employer employer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateProfile(@MappingTarget Employer entity, EmployerUpdateRequest request);
}
