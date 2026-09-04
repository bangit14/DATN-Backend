package com.backend.message_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.backend.message_service.enums.MessageType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("messages")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("conversation_id")
    private Long conversationId;

    @TableField(exist = false)
    private Conversation conversation;

    @TableField("sender_id")
    private UUID senderId;

    @TableField("message_type")
    private MessageType messageType;

    @TableField("content")
    private String content;

    @TableField(value = "sent_at", fill = FieldFill.INSERT)
    private Instant sentAt;
}
