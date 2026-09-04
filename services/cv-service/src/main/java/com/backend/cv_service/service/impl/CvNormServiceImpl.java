package com.backend.cv_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.cv_service.dto.CvNlpResultDto;
import com.backend.cv_service.dto.CvNormUpdatedEvent;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.entity.CvNorm;
import com.backend.cv_service.mapper.db.CVDbMapper;
import com.backend.cv_service.mapper.db.CvNormDbMapper;
import com.backend.cv_service.service.CvNormEventPublisher;
import com.backend.cv_service.service.CvNormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CvNormServiceImpl implements CvNormService {
    private final CvNormEventPublisher cvNormEventPublisher;
    private final CVDbMapper cvDbMapper;
    private final CvNormDbMapper cvNormDbMapper;

    @Override
    @Transactional
    public void upsertFromNlpResult(Long cvId, CvNlpResultDto result) {
        CV cv = cvDbMapper.selectById(cvId);
        if (cv == null) {
            throw new IllegalArgumentException("CV not found: " + cvId);
        }

        CvNorm cvNorm = cvNormDbMapper.selectById(cvId);
        boolean isNew = (cvNorm == null);
        if (isNew) {
            cvNorm = new CvNorm();
            cvNorm.setCvId(cvId);
            cvNorm.setCv(cv);
        }

        CvNlpResultDto.EducationPart edu = result.getEducation();
        if (edu != null) {
            cvNorm.setEducationLevel(edu.getLevel());
            cvNorm.setEducationMajors(
                    edu.getMajors() != null ? edu.getMajors() : Collections.emptyList()
            );
        } else {
            cvNorm.setEducationLevel(null);
            cvNorm.setEducationMajors(Collections.emptyList());
        }

        CvNlpResultDto.ExperiencePart exp = result.getExperience();
        if (exp != null) {
            cvNorm.setYearsTotal(exp.getYearsTotal());
            cvNorm.setExperienceTitles(
                    exp.getTitles() != null ? exp.getTitles() : Collections.emptyList()
            );
            cvNorm.setExperienceAreas(
                    exp.getAreas() != null ? exp.getAreas() : Collections.emptyList()
            );
        } else {
            cvNorm.setYearsTotal(null);
            cvNorm.setExperienceTitles(Collections.emptyList());
            cvNorm.setExperienceAreas(Collections.emptyList());
        }

        cvNorm.setSkillsNorm(
                result.getSkills() != null ? result.getSkills() : Collections.emptyList()
        );

        cvNorm.setModelVersion(result.getModelVersion());
        cvNorm.setProcessedAt(OffsetDateTime.now());

        if (isNew) {
            cvNormDbMapper.insert(cvNorm);
        } else {
            cvNormDbMapper.updateById(cvNorm);
        }

        cv.setNlpStatus("SUCCESS");
        cv.setProcessedAt(OffsetDateTime.now());
        cvDbMapper.updateById(cv);

        cvNormEventPublisher.publish(
                CvNormUpdatedEvent.builder()
                        .cvId(cvId)
                        .studentId(cv.getStudentId())
                        .skillsNorm(cvNorm.getSkillsNorm() != null ? cvNorm.getSkillsNorm() : Collections.emptyList())
                        .experienceAreas(cvNorm.getExperienceAreas() != null ? cvNorm.getExperienceAreas() : Collections.emptyList())
                        .experienceTitles(cvNorm.getExperienceTitles() != null ? cvNorm.getExperienceTitles() : Collections.emptyList())
                        .educationLevel(cvNorm.getEducationLevel())
                        .educationMajors(cvNorm.getEducationMajors() != null ? cvNorm.getEducationMajors() : Collections.emptyList())
                        .yearsTotal(null)
                        .modelVersion(cvNorm.getModelVersion())
                        .updatedAt(OffsetDateTime.now())
                        .build()
        );
    }
}
