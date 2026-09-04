package com.backend.jobservice.mapper.db;

import com.backend.jobservice.entity.SkillCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillCategoryDbMapper extends BaseMapper<SkillCategory> {
}
