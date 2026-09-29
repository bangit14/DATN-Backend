package com.backend.recruitmentservice.job.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.recruitmentservice.job.entity.JobView;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobViewDbMapper extends BaseMapper<JobView> {
}
