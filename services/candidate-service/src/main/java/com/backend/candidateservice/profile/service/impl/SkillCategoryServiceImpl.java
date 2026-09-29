package com.backend.candidateservice.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.candidateservice.profile.dto.skill.CreateCategoryRequest;
import com.backend.candidateservice.profile.dto.skill.SkillCategoryResponse;
import com.backend.candidateservice.profile.dto.skill.UpdateCategoryRequest;
import com.backend.candidateservice.profile.entity.SkillCategory;
import com.backend.candidateservice.profile.mapper.db.SkillCategoryDbMapper;
import com.backend.candidateservice.profile.service.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class SkillCategoryServiceImpl extends ServiceImpl<SkillCategoryDbMapper, SkillCategory> implements SkillCategoryService {

    private final SkillCategoryDbMapper categoryDbMapper;

    @Override
    @Transactional
    public SkillCategoryResponse createCategory(CreateCategoryRequest request) {
        Long count = categoryDbMapper.selectCount(
                new LambdaQueryWrapper<SkillCategory>()
                        .eq(SkillCategory::getName, request.getName().trim())
        );
        if (count != null && count > 0) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }

        SkillCategory category = new SkillCategory();
        category.setId(UUID.randomUUID());
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        categoryDbMapper.insert(category);
        return toResponse(category);
    }

    @Override
    @Transactional
    public SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        SkillCategory category = categoryDbMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy danh mục");
        }

        if (request.getName() != null && !request.getName().trim().equalsIgnoreCase(category.getName())) {
            Long count = categoryDbMapper.selectCount(
                    new LambdaQueryWrapper<SkillCategory>()
                            .eq(SkillCategory::getName, request.getName().trim())
            );
            if (count != null && count > 0) {
                throw new RuntimeException("Tên danh mục đã tồn tại");
            }
            category.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        categoryDbMapper.updateById(category);
        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillCategoryResponse getCategory(UUID id) {
        SkillCategory category = categoryDbMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy danh mục");
        }
        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillCategoryResponse> getAllCategories() {
        return categoryDbMapper.selectList(null).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        SkillCategory category = categoryDbMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("Không tìm thấy danh mục");
        }
        categoryDbMapper.deleteById(id);
    }

    private SkillCategoryResponse toResponse(SkillCategory entity) {
        return SkillCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
