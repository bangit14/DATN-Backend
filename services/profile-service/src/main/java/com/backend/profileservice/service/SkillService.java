package com.backend.profileservice.service;

import com.backend.profileservice.dto.skill.CreateSkillRequest;
import com.backend.profileservice.dto.skill.SkillResponse;
import com.backend.profileservice.dto.skill.UpdateSkillRequest;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.Skill;

public interface SkillService extends IService<Skill> {
    SkillResponse createSkill(CreateSkillRequest request);
    SkillResponse updateSkill(UUID id, UpdateSkillRequest request);
    SkillResponse getSkill(UUID id);
    List<SkillResponse> getAllSkills();
    List<SkillResponse> searchSkills(String keyword);
    List<SkillResponse> getSkillsByCategory(UUID categoryId);
    void deleteSkill(UUID id);
}
