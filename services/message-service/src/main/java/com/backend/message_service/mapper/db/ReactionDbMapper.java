package com.backend.message_service.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.message_service.entity.Reaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReactionDbMapper extends BaseMapper<Reaction> {
}
