package com.backend.recruitmentservice.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.recruitmentservice.job.dto.request.CreateCategoryRequest;
import com.backend.recruitmentservice.job.dto.request.UpdateCategoryRequest;
import com.backend.recruitmentservice.job.dto.response.ListDataRes;
import com.backend.recruitmentservice.job.dto.response.SkillCategoryResponse;
import com.backend.recruitmentservice.job.entity.SkillCategory;

import java.util.UUID;

public interface SkillCategoryService extends IService<SkillCategory> {
    SkillCategoryResponse createCategory(CreateCategoryRequest request);
    SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    void deleteCategory(UUID id);
    SkillCategoryResponse getCategory(UUID id);
    ListDataRes<SkillCategoryResponse> getAllCategories();
    ListDataRes<SkillCategoryResponse> getCategoriesPage(int page, int size, String keyword);
}
