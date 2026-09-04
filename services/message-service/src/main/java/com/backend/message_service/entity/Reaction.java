package com.backend.message_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.message_service.enums.ReactionType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("reactions")
public class Reaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("message_id")
    private Long messageId;

    @TableField(exist = false)
    private Message message;

    @TableField("user_id")
    private UUID userId;

    @TableField("reaction_type")
    private ReactionType reactionType;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Instant createdAt;
}
