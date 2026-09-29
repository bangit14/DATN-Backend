package com.backend.recruitmentservice.application.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.recruitmentservice.application.entity.ApplicationNote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationNoteDbMapper extends BaseMapper<ApplicationNote> {
}
