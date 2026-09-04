package com.backend.message_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("conversations")
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user1_id")
    private UUID user1Id;

    @TableField("user2_id")
    private UUID user2Id;

    @TableField("user1_last_read_message_id")
    private Long user1LastReadMessageId;

    @TableField("user2_last_read_message_id")
    private Long user2LastReadMessageId;

    @TableField("last_message_id")
    private Long lastMessageId;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Instant updatedAt;
}
