package com.backend.candidateservice.cv.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("files")
public class AppFile {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("owner_account_id")
    private UUID ownerAccountId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_key")
    private String fileUrl;

    @TableField("file_type")
    private String fileType;

    @TableField("file_size_kb")
    private Integer fileSizeKb;

    @TableField("purpose")
    private String purpose;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;
}
