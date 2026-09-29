package com.backend.recruitmentservice.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.recruitmentservice.job.enums.ImportanceLevel;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("job_skills")
public class JobSkill {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("job_id")
    private UUID jobId;

    @TableField(exist = false)
    private JobPost jobPost;

    @TableField("skill_id")
    private UUID skillId;

    @TableField("importance_level")
    private ImportanceLevel importanceLevel;

    @TableField("note")
    private String note;
}
