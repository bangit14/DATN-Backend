package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.CompanyCreateRequest;
import com.backend.candidateservice.profile.dto.request.CompanyUpdateRequest;
import com.backend.candidateservice.profile.dto.response.CompanyResponse;
import com.backend.candidateservice.profile.entity.Company;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyCreateRequest request);

    CompanyResponse toResponse(Company company);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Company target, CompanyUpdateRequest request);
}
