package com.backend.profileservice.mapper;

import com.backend.profileservice.dto.request.candidate.social.SocialLinkCreateRequest;
import com.backend.profileservice.dto.request.candidate.social.SocialLinkUpdateRequest;
import com.backend.profileservice.dto.response.candidate.social.SocialLinkResponse;
import com.backend.profileservice.entity.SocialLink;
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