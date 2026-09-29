package com.backend.candidateservice.cv.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.cv.entity.AppFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppFileDbMapper extends BaseMapper<AppFile> {
}
