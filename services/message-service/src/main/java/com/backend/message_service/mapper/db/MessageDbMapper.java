package com.backend.message_service.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.message_service.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageDbMapper extends BaseMapper<Message> {
}
