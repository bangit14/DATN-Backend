package com.backend.profileservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.profileservice.entity.Experience;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExperienceDbMapper extends BaseMapper<Experience> {
}
