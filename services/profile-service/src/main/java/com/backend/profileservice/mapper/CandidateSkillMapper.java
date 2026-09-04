package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.candidate.skill.CandidateSkillCreateRequest;
import com.backend.profileservice.dto.response.candidate.skill.CandidateSkillResponse;
import com.backend.profileservice.entity.CandidateSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateSkillMapper {

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    CandidateSkill toEntity(CandidateSkillCreateRequest request);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "skillName")
    @Mapping(source = "skill.category.name", target = "categoryName")
    CandidateSkillResponse toResponse(CandidateSkill entity);
}
