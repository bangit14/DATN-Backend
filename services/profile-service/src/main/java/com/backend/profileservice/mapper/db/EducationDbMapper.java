package com.backend.profileservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.profileservice.entity.Education;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EducationDbMapper extends BaseMapper<Education> {
}
