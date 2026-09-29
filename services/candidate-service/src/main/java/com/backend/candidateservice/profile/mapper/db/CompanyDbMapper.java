package com.backend.candidateservice.profile.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.profile.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyDbMapper extends BaseMapper<Company> {
}
