package com.backend.candidateservice.cv.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.candidateservice.cv.dto.MappedCvQuery;
import com.backend.candidateservice.cv.entity.CV;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CVDbMapper extends BaseMapper<CV> {

    List<CV> getCvPageForEmployer(IPage<CV> page, @Param("query") MappedCvQuery query);
}
