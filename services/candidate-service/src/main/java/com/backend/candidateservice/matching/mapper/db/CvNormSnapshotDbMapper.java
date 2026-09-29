package com.backend.candidateservice.matching.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.matching.entity.CvNormSnapshotEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper
public interface CvNormSnapshotDbMapper extends BaseMapper<CvNormSnapshotEntity> {

    @Insert("""
        INSERT INTO cv_norm_snapshot (
            cv_id, student_id,
            skills_norm, experience_areas, experience_titles,
            education_level, education_majors,
            years_total, model_version, updated_at
        )
        VALUES (
            #{cvId}, #{studentId},
            CAST(#{skillsNorm} AS jsonb),
            CAST(#{experienceAreas} AS jsonb),
            CAST(#{experienceTitles} AS jsonb),
            #{educationLevel},
            CAST(#{educationMajors} AS jsonb),
            #{yearsTotal},
            #{modelVersion},
            #{updatedAt}
        )
        ON CONFLICT (cv_id)
        DO UPDATE SET
            student_id        = EXCLUDED.student_id,
            skills_norm       = EXCLUDED.skills_norm,
            experience_areas  = EXCLUDED.experience_areas,
            experience_titles = EXCLUDED.experience_titles,
            education_level   = EXCLUDED.education_level,
            education_majors  = EXCLUDED.education_majors,
            years_total       = EXCLUDED.years_total,
            model_version     = EXCLUDED.model_version,
            updated_at        = EXCLUDED.updated_at
    """)
    void upsert(
            @Param("cvId") Long cvId,
            @Param("studentId") UUID studentId,
            @Param("skillsNorm") String skillsNorm,
            @Param("experienceAreas") String experienceAreas,
            @Param("experienceTitles") String experienceTitles,
            @Param("educationLevel") String educationLevel,
            @Param("educationMajors") String educationMajors,
            @Param("yearsTotal") BigDecimal yearsTotal,
            @Param("modelVersion") String modelVersion,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}
