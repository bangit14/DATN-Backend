package com.backend.jobservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.jobservice.dto.request.MappedJobPostQuery;
import com.backend.jobservice.entity.JobPost;
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
