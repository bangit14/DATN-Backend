package com.backend.candidateservice.profile.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.profile.entity.SkillCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillCategoryDbMapper extends BaseMapper<SkillCategory> {
}
