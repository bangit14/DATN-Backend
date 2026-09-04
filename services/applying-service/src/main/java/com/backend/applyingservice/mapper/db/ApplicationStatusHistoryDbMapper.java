package com.backend.applyingservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.applyingservice.entity.ApplicationStatusHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationStatusHistoryDbMapper extends BaseMapper<ApplicationStatusHistory> {
}
