package com.backend.jobservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.jobservice.dto.request.CreateCategoryRequest;
import com.backend.jobservice.dto.request.UpdateCategoryRequest;
import com.backend.jobservice.dto.response.ListDataRes;
import com.backend.jobservice.dto.response.SkillCategoryResponse;
import com.backend.jobservice.entity.SkillCategory;

import java.util.UUID;

public interface SkillCategoryService extends IService<SkillCategory> {
    SkillCategoryResponse createCategory(CreateCategoryRequest request);
    SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    void deleteCategory(UUID id);
    SkillCategoryResponse getCategory(UUID id);
    ListDataRes<SkillCategoryResponse> getAllCategories();
    ListDataRes<SkillCategoryResponse> getCategoriesPage(int page, int size, String keyword);
}
