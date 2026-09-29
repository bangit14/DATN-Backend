package com.backend.candidateservice.profile.service;

import com.backend.candidateservice.profile.dto.skill.CreateCategoryRequest;
import com.backend.candidateservice.profile.dto.skill.SkillCategoryResponse;
import com.backend.candidateservice.profile.dto.skill.UpdateCategoryRequest;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.candidateservice.profile.entity.SkillCategory;

public interface SkillCategoryService extends IService<SkillCategory> {
    SkillCategoryResponse createCategory(CreateCategoryRequest request);
    SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    SkillCategoryResponse getCategory(UUID id);
    List<SkillCategoryResponse> getAllCategories();
    void deleteCategory(UUID id);
}
