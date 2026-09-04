package com.backend.profileservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.profileservice.entity.Employer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployerDbMapper extends BaseMapper<Employer> {
}
