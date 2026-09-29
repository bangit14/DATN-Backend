package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.candidate.CandidateCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.CandidateUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.CandidateResponse;
import com.backend.candidateservice.profile.entity.Candidate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        EducationMapper.class,
        ExperienceMapper.class,
        ProjectMapper.class,
        SocialLinkMapper.class,
        CandidateSkillMapper.class
})
public interface CandidateMapper {

    Candidate toEntity(CandidateCreateRequest request);

    CandidateResponse toResponse(Candidate entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Candidate candidate, CandidateUpdateRequest request);
}
