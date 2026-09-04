package com.backend.cv_service.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.cv_service.entity.CvTemplate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CvTemplateDbMapper extends BaseMapper<CvTemplate> {
}
