package com.backend.cv_service.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.cv_service.dto.MappedCvQuery;
import com.backend.cv_service.entity.CV;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CVDbMapper extends BaseMapper<CV> {

    List<CV> getCvPageForEmployer(IPage<CV> page, @Param("query") MappedCvQuery query);
}
