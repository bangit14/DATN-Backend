package com.backend.recruitmentservice.job.mapper.db;

import com.backend.recruitmentservice.job.entity.SkillCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkillCategoryDbMapper extends BaseMapper<SkillCategory> {
}
