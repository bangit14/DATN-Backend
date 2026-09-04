package com.backend.profileservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.profileservice.entity.SkillCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillCategoryDbMapper extends BaseMapper<SkillCategory> {
}
