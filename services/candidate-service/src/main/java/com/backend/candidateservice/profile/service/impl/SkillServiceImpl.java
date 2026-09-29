package com.backend.candidateservice.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.candidateservice.profile.dto.skill.CreateSkillRequest;
import com.backend.candidateservice.profile.dto.skill.SkillResponse;
import com.backend.candidateservice.profile.dto.skill.UpdateSkillRequest;
import com.backend.candidateservice.profile.entity.Skill;
import com.backend.candidateservice.profile.entity.SkillCategory;
import com.backend.candidateservice.profile.mapper.db.SkillCategoryDbMapper;
import com.backend.candidateservice.profile.mapper.db.SkillDbMapper;
import com.backend.candidateservice.profile.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl extends ServiceImpl<SkillDbMapper, Skill> implements SkillService {

    private final SkillDbMapper skillDbMapper;
    private final SkillCategoryDbMapper categoryDbMapper;

    @Override
    @Transactional
    public SkillResponse createSkill(CreateSkillRequest request) {
        Long count = skillDbMapper.selectCount(
                new LambdaQueryWrapper<Skill>()
                        .eq(Skill::getName, request.getName().trim())
        );
        if (count != null && count > 0) {
            throw new RuntimeException("Kỹ năng đã tồn tại: " + request.getName());
        }

        SkillCategory category = categoryDbMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new RuntimeException("Không tìm thấy danh mục");
        }

        Skill skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName(request.getName().trim());
        skill.setCategoryId(category.getId());
        skill.setDescription(request.getDescription());
        skill.setCreatedAt(Instant.now());
        skill.setUpdatedAt(Instant.now());

        skillDbMapper.insert(skill);
        skill.setCategory(category);

        return toResponse(skill);
    }

    @Override
    @Transactional
    public SkillResponse updateSkill(UUID id, UpdateSkillRequest request) {
        Skill skill = skillDbMapper.selectById(id);
        if (skill == null) {
            throw new RuntimeException("Không tìm thấy kỹ năng");
        }

        if (request.getName() != null && !request.getName().trim().equalsIgnoreCase(skill.getName())) {
            Long count = skillDbMapper.selectCount(
                    new LambdaQueryWrapper<Skill>()
                            .eq(Skill::getName, request.getName().trim())
            );
            if (count != null && count > 0) {
                throw new RuntimeException("Kỹ năng đã tồn tại: " + request.getName());
            }
            skill.setName(request.getName().trim());
        }

        if (request.getCategoryId() != null) {
            SkillCategory category = categoryDbMapper.selectById(request.getCategoryId());
            if (category == null) {
                throw new RuntimeException("Không tìm thấy danh mục");
            }
            skill.setCategoryId(category.getId());
            skill.setCategory(category);
        }

        if (request.getDescription() != null) {
            skill.setDescription(request.getDescription());
        }

        skillDbMapper.updateById(skill);
        if (skill.getCategory() == null && skill.getCategoryId() != null) {
            skill.setCategory(categoryDbMapper.selectById(skill.getCategoryId()));
        }

        return toResponse(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getSkill(UUID id) {
        Skill skill = skillDbMapper.selectById(id);
        if (skill == null) {
            throw new RuntimeException("Không tìm thấy kỹ năng");
        }
        if (skill.getCategoryId() != null) {
            skill.setCategory(categoryDbMapper.selectById(skill.getCategoryId()));
        }
        return toResponse(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        List<Skill> skills = skillDbMapper.selectList(null);
        populateCategories(skills);
        return skills.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> searchSkills(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSkills();
        }
        List<Skill> skills = skillDbMapper.selectList(
                new LambdaQueryWrapper<Skill>().like(Skill::getName, keyword.trim())
        );
        populateCategories(skills);
        return skills.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getSkillsByCategory(UUID categoryId) {
        List<Skill> skills = skillDbMapper.selectList(
                new LambdaQueryWrapper<Skill>().eq(Skill::getCategoryId, categoryId)
        );
        populateCategories(skills);
        return skills.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSkill(UUID id) {
        Skill skill = skillDbMapper.selectById(id);
        if (skill == null) {
            throw new RuntimeException("Không tìm thấy kỹ năng");
        }
        skillDbMapper.deleteById(id);
    }

    private void populateCategories(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) return;
        List<UUID> categoryIds = skills.stream()
                .map(Skill::getCategoryId)
                .filter(cid -> cid != null)
                .distinct()
                .toList();
        if (categoryIds.isEmpty()) return;

        List<SkillCategory> categories = categoryDbMapper.selectBatchIds(categoryIds);
        Map<UUID, SkillCategory> categoryMap = categories.stream()
                .collect(Collectors.toMap(SkillCategory::getId, c -> c));

        skills.forEach(s -> {
            if (s.getCategoryId() != null) {
                s.setCategory(categoryMap.get(s.getCategoryId()));
            }
        });
    }

    private SkillResponse toResponse(Skill entity) {
        return SkillResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : entity.getCategoryId())
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
