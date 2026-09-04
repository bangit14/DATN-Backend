package com.backend.jobservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.jobservice.dto.request.CreateSkillRequest;
import com.backend.jobservice.dto.request.UpdateSkillRequest;
import com.backend.jobservice.dto.response.ListDataRes;
import com.backend.jobservice.dto.response.SkillResponse;
import com.backend.jobservice.entity.Skill;

import java.util.List;
import java.util.UUID;

public interface SkillService extends IService<Skill> {
    SkillResponse createSkill(CreateSkillRequest request);
    SkillResponse updateSkill(UUID id, UpdateSkillRequest request);
    void deleteSkill(UUID id);
    SkillResponse getSkill(UUID id);
    ListDataRes<SkillResponse> searchSkills(String keyword);
    ListDataRes<SkillResponse> searchSkills(String keyword, int page, int size, UUID categoryId);
    ListDataRes<SkillResponse> getSkillsByCategory(UUID categoryId);
    List<SkillResponse> getSkillsByIds(List<UUID> ids);
}
