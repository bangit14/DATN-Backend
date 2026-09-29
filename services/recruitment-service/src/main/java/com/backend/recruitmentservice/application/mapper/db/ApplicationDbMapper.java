package com.backend.recruitmentservice.application.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.recruitmentservice.application.entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationDbMapper extends BaseMapper<Application> {
}
