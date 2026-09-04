package com.backend.jobservice.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backend.jobservice.dto.response.SkillResponse;
import com.backend.jobservice.entity.Skill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface SkillDbMapper extends BaseMapper<Skill> {

    IPage<SkillResponse> searchSkillsPage(
            Page<?> page,
            @Param("keyword") String keyword,
            @Param("categoryId") UUID categoryId
    );

    List<SkillResponse> searchSkillsWithCategory(
            @Param("keyword") String keyword,
            @Param("categoryId") UUID categoryId
    );

    SkillResponse getSkillDetailWithCategory(@Param("id") UUID id);

    List<SkillResponse> getSkillsBatchWithCategory(@Param("ids") List<UUID> ids);
}
