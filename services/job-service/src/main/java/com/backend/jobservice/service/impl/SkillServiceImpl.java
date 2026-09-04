package com.backend.jobservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.jobservice.dto.request.CreateSkillRequest;
import com.backend.jobservice.dto.request.UpdateSkillRequest;
import com.backend.jobservice.dto.response.ListDataRes;
import com.backend.jobservice.dto.response.SkillCategoryResponse;
import com.backend.jobservice.dto.response.SkillResponse;
import com.backend.jobservice.entity.Skill;
import com.backend.jobservice.entity.SkillCategory;
import com.backend.jobservice.enums.ErrorCode;
import com.backend.jobservice.exception.AppException;
import com.backend.jobservice.mapper.db.SkillCategoryDbMapper;
import com.backend.jobservice.mapper.db.SkillDbMapper;
import com.backend.jobservice.service.SkillService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl extends ServiceImpl<SkillDbMapper, Skill> implements SkillService {

    private final SkillDbMapper skillDbMapper;
    private final SkillCategoryDbMapper categoryDbMapper;

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public SkillResponse createSkill(CreateSkillRequest request) {
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Creating skill with name={}", request.getName());

        boolean exists = skillDbMapper.exists(
                new LambdaQueryWrapper<Skill>()
                        .apply("LOWER(name) = LOWER({0})", request.getName().trim())
        );
        if (exists) {
            throw new AppException(ErrorCode.SKILL_ALREADY_EXIST);
        }

        SkillCategory category = categoryDbMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Skill skill = new Skill();
        BeanUtils.copyProperties(request, skill);
        skill.setName(request.getName().trim());
        skill.setCategoryId(category.getId());
        skill.setCreatedAt(Instant.now());
        skill.setUpdatedAt(Instant.now());
        skillDbMapper.insert(skill);

        SkillResponse response = new SkillResponse();
        BeanUtils.copyProperties(skill, response);
        response.setCategoryName(category.getName());
        response.setCategory(SkillCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build());
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public SkillResponse updateSkill(UUID id, UpdateSkillRequest request) {
        Objects.requireNonNull(id, "ID must not be null");
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Updating skill id={}", id);

        Skill skill = skillDbMapper.selectById(id);
        if (skill == null) {
            throw new AppException(ErrorCode.SKILL_NOT_FOUND);
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            boolean exists = skillDbMapper.exists(
                    new LambdaQueryWrapper<Skill>()
                            .apply("LOWER(name) = LOWER({0})", request.getName().trim())
                            .ne(Skill::getId, id)
            );
            if (exists) {
                throw new AppException(ErrorCode.SKILL_ALREADY_EXIST);
            }
        }

        BeanUtils.copyProperties(request, skill);
        if (request.getName() != null) {
            skill.setName(request.getName().trim());
        }

        SkillCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryDbMapper.selectById(request.getCategoryId());
            if (category == null) {
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            skill.setCategoryId(category.getId());
        } else if (skill.getCategoryId() != null) {
            category = categoryDbMapper.selectById(skill.getCategoryId());
        }

        skill.setUpdatedAt(Instant.now());
        skillDbMapper.updateById(skill);

        SkillResponse response = new SkillResponse();
        BeanUtils.copyProperties(skill, response);
        if (category != null) {
            response.setCategoryName(category.getName());
            response.setCategory(SkillCategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }
        return response;
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'SYSTEM_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public void deleteSkill(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        log.info("Deleting skill id={}", id);

        Skill skill = skillDbMapper.selectById(id);
        if (skill == null) {
            throw new AppException(ErrorCode.SKILL_NOT_FOUND);
        }

        skillDbMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getSkill(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        SkillResponse response = skillDbMapper.getSkillDetailWithCategory(id);
        if (response == null) {
            throw new AppException(ErrorCode.SKILL_NOT_FOUND);
        }
        if (response.getCategoryId() != null || response.getCategoryName() != null) {
            response.setCategory(SkillCategoryResponse.builder()
                    .id(response.getCategoryId())
                    .name(response.getCategoryName())
                    .build());
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<SkillResponse> searchSkills(String keyword) {
        List<SkillResponse> list = skillDbMapper.searchSkillsWithCategory(keyword, null);
        if (list == null || list.isEmpty()) {
            return ListDataRes.of(Collections.emptyList());
        }
        list.forEach(r -> {
            if (r.getCategoryId() != null || r.getCategoryName() != null) {
                r.setCategory(SkillCategoryResponse.builder()
                        .id(r.getCategoryId())
                        .name(r.getCategoryName())
                        .build());
            }
        });
        return ListDataRes.of(list);
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<SkillResponse> searchSkills(String keyword, int page, int size, UUID categoryId) {
        Page<SkillResponse> mpPage = new Page<>(Math.max(1, page), Math.max(1, size));
        IPage<SkillResponse> result = skillDbMapper.searchSkillsPage(mpPage, keyword, categoryId);
        List<SkillResponse> list = result.getRecords();
        if (list != null) {
            list.forEach(r -> {
                if (r.getCategoryId() != null || r.getCategoryName() != null) {
                    r.setCategory(SkillCategoryResponse.builder()
                            .id(r.getCategoryId())
                            .name(r.getCategoryName())
                            .build());
                }
            });
        }
        return ListDataRes.of(list != null ? list : Collections.emptyList(), result);
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<SkillResponse> getSkillsByCategory(UUID categoryId) {
        Objects.requireNonNull(categoryId, "CategoryId must not be null");
        SkillCategory cat = categoryDbMapper.selectById(categoryId);
        if (cat == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        List<SkillResponse> list = skillDbMapper.searchSkillsWithCategory(null, categoryId);
        if (list == null || list.isEmpty()) {
            return ListDataRes.of(Collections.emptyList());
        }
        list.forEach(r -> {
            r.setCategory(SkillCategoryResponse.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .description(cat.getDescription())
                    .build());
            r.setCategoryName(cat.getName());
        });
        return ListDataRes.of(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getSkillsByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<SkillResponse> list = skillDbMapper.getSkillsBatchWithCategory(ids);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        list.forEach(r -> {
            if (r.getCategoryId() != null || r.getCategoryName() != null) {
                r.setCategory(SkillCategoryResponse.builder()
                        .id(r.getCategoryId())
                        .name(r.getCategoryName())
                        .build());
            }
        });
        return list;
    }
}
