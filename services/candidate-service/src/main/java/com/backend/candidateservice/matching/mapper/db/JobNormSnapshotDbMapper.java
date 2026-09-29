package com.backend.candidateservice.matching.mapper.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.candidateservice.matching.entity.JobNormSnapshotEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper
public interface JobNormSnapshotDbMapper extends BaseMapper<JobNormSnapshotEntity> {

    @Insert("""
        INSERT INTO job_norm_snapshot (
            internship_post_id, company_id,
            skills_norm, experience_areas, experience_titles,
            education_level, education_majors,
            min_years, work_mode, location_lat, location_lon,
            updated_at
        )
        VALUES (
            #{postId}, #{companyId},
            CAST(#{skillsNorm} AS jsonb),
            CAST(#{experienceAreas} AS jsonb),
            CAST(#{experienceTitles} AS jsonb),
            #{educationLevel},
            CAST(#{educationMajors} AS jsonb),
            #{minYears},
            #{workMode},
            #{lat},
            #{lon},
            #{updatedAt}
        )
        ON CONFLICT (internship_post_id)
        DO UPDATE SET
            company_id        = EXCLUDED.company_id,
            skills_norm       = EXCLUDED.skills_norm,
            experience_areas  = EXCLUDED.experience_areas,
            experience_titles = EXCLUDED.experience_titles,
            education_level   = EXCLUDED.education_level,
            education_majors  = EXCLUDED.education_majors,
            min_years         = EXCLUDED.min_years,
            work_mode         = EXCLUDED.work_mode,
            location_lat      = EXCLUDED.location_lat,
            location_lon      = EXCLUDED.location_lon,
            updated_at        = EXCLUDED.updated_at
    """)
    void upsert(
            @Param("postId") UUID postId,
            @Param("companyId") UUID companyId,
            @Param("skillsNorm") String skillsNorm,
            @Param("experienceAreas") String experienceAreas,
            @Param("experienceTitles") String experienceTitles,
            @Param("educationLevel") String educationLevel,
            @Param("educationMajors") String educationMajors,
            @Param("minYears") BigDecimal minYears,
            @Param("workMode") String workMode,
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}
