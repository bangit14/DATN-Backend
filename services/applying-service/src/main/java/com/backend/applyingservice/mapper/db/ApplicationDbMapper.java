package com.backend.applyingservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.applyingservice.entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationDbMapper extends BaseMapper<Application> {
}
