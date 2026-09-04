package com.backend.authservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.authservice.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {
}
