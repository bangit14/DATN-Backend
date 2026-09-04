package com.backend.message_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.message_service.client.ProfileFeignClient;
import com.backend.message_service.dto.request.FindConversationRequest;
import com.backend.message_service.dto.response.*;
import com.backend.message_service.entity.Conversation;
import com.backend.message_service.entity.Message;
import com.backend.message_service.enums.MessageType;
import com.backend.message_service.mapper.db.ConversationDbMapper;
import com.backend.message_service.mapper.db.MessageDbMapper;
import com.backend.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl extends ServiceImpl<ConversationDbMapper, Conversation> implements ConversationService {

    private final ConversationDbMapper conversationDbMapper;
    private final MessageDbMapper messageDbMapper;
    private final ProfileFeignClient profileFeignClient;

    @Override
    @Transactional
    public ConversationResponse findOrCreateConversation(UUID UID1, FindConversationRequest request) {
        ApiResponse<List<UserBasicInfoResponse>> response = profileFeignClient.getStudentsBatch(List.of(request.getUser2Id()));
        UUID other = request.getUser2Id();
        UUID min = UID1.compareTo(other) <= 0 ? UID1 : other;
        UUID max = UID1.compareTo(other) <= 0 ? other : UID1;

        request.setUser1Id(min);
        request.setUser2Id(max);

        Conversation conv = conversationDbMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUser1Id, request.getUser1Id())
                        .eq(Conversation::getUser2Id, request.getUser2Id())
        );

        if (conv != null) {
            return convertToConversationResponse(conv, UID1, response.getData().getFirst());
        }

        Conversation newConversation = new Conversation();
        newConversation.setUser1Id(request.getUser1Id());
        newConversation.setUser2Id(request.getUser2Id());

        conversationDbMapper.insert(newConversation);

        return convertToConversationResponse(newConversation, UID1, response.getData().getFirst());
    }

    private ConversationResponse convertToConversationResponse(Conversation conversation, UUID currentUserId, UserBasicInfoResponse userBasicInfoResponse) {
        ConversationResponse res = new ConversationResponse();
        res.setId(conversation.getId());
        res.setOtherUserInfo(userBasicInfoResponse);
        UUID receiverId = conversation.getUser1Id().equals(currentUserId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(receiverId);
        res.setReceiver(userResponse);

        if (conversation.getLastMessageId() != null) {
            Message last = messageDbMapper.selectById(conversation.getLastMessageId());
            res.setLastMessage(convertToMessageResponse(last));
        } else {
            res.setLastMessage(null);
        }

        res.setUnreadCount(0);
        return res;
    }

    private MessageResponse convertToMessageResponse(Message message) {
        if (message == null) return null;

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());

        response.setContent(message.getMessageType() == MessageType.RECALLED ? "" : message.getContent());
        response.setMessageType(message.getMessageType().name());
        response.setSentAt(message.getSentAt());

        response.setSenderId(message.getSenderId());

        return response;
    }

    @Transactional
    public List<ConversationResponse> getConversationsByUserId(UUID userId) {
        List<Conversation> listConversationRepo = conversationDbMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUser1Id, userId)
                        .or()
                        .eq(Conversation::getUser2Id, userId)
                        .orderByDesc(Conversation::getUpdatedAt)
        );

        if (listConversationRepo.isEmpty()) return List.of();

        List<UUID> otherUserIds = listConversationRepo.stream()
                .map(c -> c.getUser1Id().equals(userId) ? c.getUser2Id() : c.getUser1Id())
                .distinct()
                .toList();

        ApiResponse<List<UserBasicInfoResponse>> response = profileFeignClient.getStudentsBatch(otherUserIds);

        Map<UUID, UserBasicInfoResponse> userInfoMap = (response != null && response.getData() != null)
                ? response.getData().stream().collect(Collectors.toMap(UserBasicInfoResponse::getUserId, Function.identity(), (a, b) -> a))
                : Map.of();

        List<ConversationResponse> res = new ArrayList<>();
        for (Conversation c : listConversationRepo) {
            UUID otherUserId = c.getUser1Id().equals(userId) ? c.getUser2Id() : c.getUser1Id();
            UserBasicInfoResponse userInfo = userInfoMap.get(otherUserId);

            res.add(convertToConversationResponse(c, userId, userInfo));
        }
        return res;
    }
}