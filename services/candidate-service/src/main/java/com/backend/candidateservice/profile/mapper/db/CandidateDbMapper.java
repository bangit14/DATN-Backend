package com.backend.candidateservice.profile.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.profile.entity.Candidate;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CandidateDbMapper extends BaseMapper<Candidate> {
}
