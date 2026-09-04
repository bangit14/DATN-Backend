package com.backend.cv_service.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.cv_service.entity.CvNorm;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CvNormDbMapper extends BaseMapper<CvNorm> {
}
