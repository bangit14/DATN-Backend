package com.backend.message_service.service;

import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.ConversationResponse;

import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.message_service.entity.Conversation;

public interface ConversationService extends IService<Conversation> {
    ConversationResponse findOrCreateConversation(UUID UID1, FindConversationRequest request);
    List<ConversationResponse> getConversationsByUserId(UUID userId);
}
