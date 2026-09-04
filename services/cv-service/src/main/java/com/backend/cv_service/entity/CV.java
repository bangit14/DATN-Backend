package com.backend.cv_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("cvs")
public class CV {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private UUID studentId;

    @TableField("cv_name")
    private String cvName;

    @TableField("template_id")
    private UUID templateId;

    @TableField("content_json")
    private String contentJson;

    @TableField("pdf_url")
    private String pdfUrl;

    @TableField("cv_url")
    private String cvUrl;

    @Builder.Default
    @TableField("is_default")
    private boolean isDefault = false;

    @TableField("raw_text")
    private String rawText;

    @TableField("nlp_status")
    private String nlpStatus;

    @TableField("processed_at")
    private OffsetDateTime processedAt;

    @Builder.Default
    @TableField("created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Builder.Default
    @TableField("updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @TableField(exist = false)
    private CvNorm cvNorm;
}
