package com.backend.jobservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.jobservice.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobPostDbMapper extends BaseMapper<JobPost> {
}
