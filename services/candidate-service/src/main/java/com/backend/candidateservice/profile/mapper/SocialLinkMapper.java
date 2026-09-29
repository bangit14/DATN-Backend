package com.backend.candidateservice.profile.mapper;

import com.backend.candidateservice.profile.dto.request.candidate.social.SocialLinkCreateRequest;
import com.backend.candidateservice.profile.dto.request.candidate.social.SocialLinkUpdateRequest;
import com.backend.candidateservice.profile.dto.response.candidate.social.SocialLinkResponse;
import com.backend.candidateservice.profile.entity.SocialLink;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SocialLinkMapper {

    @Mapping(target = "platform", source = "type")
    SocialLink toEntity(SocialLinkCreateRequest request);

    @Mapping(target = "type", source = "platform")
    SocialLinkResponse toResponse(SocialLink entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "platform", source = "type")
    void updateEntity(@MappingTarget SocialLink socialLink, SocialLinkUpdateRequest request);
}