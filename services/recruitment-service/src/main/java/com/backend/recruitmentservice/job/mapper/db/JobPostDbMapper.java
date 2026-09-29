package com.backend.recruitmentservice.job.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.recruitmentservice.job.dto.request.MappedJobPostQuery;
import com.backend.recruitmentservice.job.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobPostDbMapper extends BaseMapper<JobPost> {

    List<JobPost> getJobPostPage(
            IPage<JobPost> page,
            @Param("query") MappedJobPostQuery query
    );
}
