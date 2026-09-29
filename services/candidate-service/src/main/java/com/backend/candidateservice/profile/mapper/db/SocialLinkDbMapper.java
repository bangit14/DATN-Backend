package com.backend.candidateservice.profile.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.profile.entity.SocialLink;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialLinkDbMapper extends BaseMapper<SocialLink> {
}
