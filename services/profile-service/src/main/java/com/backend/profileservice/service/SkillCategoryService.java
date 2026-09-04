package com.backend.profileservice.service;

import com.backend.profileservice.dto.skill.CreateCategoryRequest;
import com.backend.profileservice.dto.skill.SkillCategoryResponse;
import com.backend.profileservice.dto.skill.UpdateCategoryRequest;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.profileservice.entity.SkillCategory;

public interface SkillCategoryService extends IService<SkillCategory> {
    SkillCategoryResponse createCategory(CreateCategoryRequest request);
    SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    SkillCategoryResponse getCategory(UUID id);
    List<SkillCategoryResponse> getAllCategories();
    void deleteCategory(UUID id);
}
