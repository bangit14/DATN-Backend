package com.backend.recruitmentservice.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.recruitmentservice.job.dto.request.CreateCategoryRequest;
import com.backend.recruitmentservice.job.dto.request.UpdateCategoryRequest;
import com.backend.recruitmentservice.job.dto.response.ListDataRes;
import com.backend.recruitmentservice.job.dto.response.SkillCategoryResponse;
import com.backend.recruitmentservice.job.entity.Skill;
import com.backend.recruitmentservice.job.entity.SkillCategory;
import com.backend.recruitmentservice.job.enums.ErrorCode;
import com.backend.recruitmentservice.job.exception.AppException;
import com.backend.recruitmentservice.job.mapper.db.SkillCategoryDbMapper;
import com.backend.recruitmentservice.job.mapper.db.SkillDbMapper;
import com.backend.recruitmentservice.job.service.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillCategoryServiceImpl extends ServiceImpl<SkillCategoryDbMapper, SkillCategory> implements SkillCategoryService {

    private final SkillCategoryDbMapper skillCategoryDbMapper;
    private final SkillDbMapper skillDbMapper;

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public SkillCategoryResponse createCategory(CreateCategoryRequest request) {
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Creating skill category with name={}", request.getName());

        boolean exists = skillCategoryDbMapper.exists(
                new LambdaQueryWrapper<SkillCategory>()
                        .apply("LOWER(name) = LOWER({0})", request.getName().trim())
        );
        if (exists) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXIST);
        }

        SkillCategory entity = new SkillCategory();
        BeanUtils.copyProperties(request, entity);
        entity.setName(request.getName().trim());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        skillCategoryDbMapper.insert(entity);

        SkillCategoryResponse response = new SkillCategoryResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public SkillCategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        Objects.requireNonNull(id, "ID must not be null");
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Updating skill category id={}", id);

        SkillCategory category = skillCategoryDbMapper.selectById(id);
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            boolean exists = skillCategoryDbMapper.exists(
                    new LambdaQueryWrapper<SkillCategory>()
                            .apply("LOWER(name) = LOWER({0})", request.getName().trim())
                            .ne(SkillCategory::getId, id)
            );
            if (exists) {
                throw new AppException(ErrorCode.CATEGORY_ALREADY_EXIST);
            }
        }

        BeanUtils.copyProperties(request, category);
        if (request.getName() != null) {
            category.setName(request.getName().trim());
        }
        category.setUpdatedAt(Instant.now());
        skillCategoryDbMapper.updateById(category);

        SkillCategoryResponse response = new SkillCategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        log.info("Deleting skill category id={}", id);

        SkillCategory category = skillCategoryDbMapper.selectById(id);
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        boolean hasSkills = skillDbMapper.exists(
                new LambdaQueryWrapper<Skill>().eq(Skill::getCategoryId, id)
        );
        if (hasSkills) {
            throw new AppException(ErrorCode.CATEGORY_HAS_SKILLS);
        }

        skillCategoryDbMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillCategoryResponse getCategory(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        SkillCategory category = skillCategoryDbMapper.selectById(id);
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        SkillCategoryResponse response = new SkillCategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<SkillCategoryResponse> getAllCategories() {
        List<SkillCategory> list = skillCategoryDbMapper.selectList(
                new LambdaQueryWrapper<SkillCategory>().orderByAsc(SkillCategory::getName)
        );
        if (list == null || list.isEmpty()) {
            return ListDataRes.of(Collections.emptyList());
        }
        List<SkillCategoryResponse> responses = list.stream().map(c -> {
            SkillCategoryResponse res = new SkillCategoryResponse();
            BeanUtils.copyProperties(c, res);
            return res;
        }).collect(Collectors.toList());

        return ListDataRes.of(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<SkillCategoryResponse> getCategoriesPage(int page, int size, String keyword) {
        Page<SkillCategory> mpPage = new Page<>(Math.max(1, page), Math.max(1, size));
        LambdaQueryWrapper<SkillCategory> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.apply("LOWER(name) LIKE LOWER({0})", "%" + keyword.trim() + "%");
        }
        wrapper.orderByAsc(SkillCategory::getName);

        Page<SkillCategory> result = skillCategoryDbMapper.selectPage(mpPage, wrapper);
        List<SkillCategoryResponse> responses = result.getRecords().stream().map(c -> {
            SkillCategoryResponse res = new SkillCategoryResponse();
            BeanUtils.copyProperties(c, res);
            return res;
        }).collect(Collectors.toList());

        return ListDataRes.of(responses, result);
    }
}
