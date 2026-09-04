package com.backend.authservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.authservice.entity.PasswordReset;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordResetMapper extends BaseMapper<PasswordReset> {
}
