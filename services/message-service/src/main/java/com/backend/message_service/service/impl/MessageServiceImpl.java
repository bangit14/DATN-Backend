package com.backend.message_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.message_service.dto.WebSocketEvent;
import com.backend.message_service.dto.request.CreateReactionRequest;
import com.backend.message_service.dto.request.SendMessageRequest;
import com.backend.message_service.dto.response.MessageResponse;
import com.backend.message_service.dto.response.ReactionResponse;
import com.backend.message_service.dto.response.UserResponse;
import com.backend.message_service.entity.Conversation;
import com.backend.message_service.entity.Message;
import com.backend.message_service.entity.Reaction;
import com.backend.message_service.enums.MessageType;
import com.backend.message_service.enums.ReactionType;
import com.backend.message_service.mapper.db.ConversationDbMapper;
import com.backend.message_service.mapper.db.MessageDbMapper;
import com.backend.message_service.mapper.db.ReactionDbMapper;
import com.backend.message_service.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageDbMapper, Message> implements MessageService {

    private final MessageDbMapper messageDbMapper;
    private final ConversationDbMapper conversationDbMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReactionDbMapper reactionDbMapper;

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID senderId, SendMessageRequest request) {
        Conversation conversation = conversationDbMapper.selectById(request.getConversationId());
        if (conversation == null) {
            throw new IllegalArgumentException("Không tìm thấy cuộc trò chuyện với ID: " + request.getConversationId());
        }

        if (!senderId.equals(conversation.getUser1Id()) && !senderId.equals(conversation.getUser2Id())) {
            throw new IllegalArgumentException("Bạn không thuộc cuộc trò chuyện này");
        }

        Message newMessage = new Message();
        newMessage.setConversationId(conversation.getId());
        newMessage.setSenderId(senderId);
        newMessage.setContent(request.getContent());
        newMessage.setMessageType(MessageType.valueOf(request.getMessageType().toUpperCase()));
        newMessage.setSentAt(Instant.now());

        messageDbMapper.insert(newMessage);

        conversation.setLastMessageId(newMessage.getId());
        conversation.setUpdatedAt(Instant.now());
        conversationDbMapper.updateById(conversation);

        MessageResponse response = convertToMessageResponse(newMessage);

        String destination = "/topic/conversation/" + response.getConversationId();
        WebSocketEvent<MessageResponse> event = new WebSocketEvent<>("NEW_MESSAGE", response);
        messagingTemplate.convertAndSend(destination, event);

        return response;
    }

    @Override
    @Transactional
    public MessageResponse recallMessage(Long messageId, UUID userId) {
        Message messageToRecall = messageDbMapper.selectById(messageId);
        if (messageToRecall == null) {
            throw new IllegalArgumentException("Không tìm thấy tin nhắn với ID: " + messageId);
        }

        if (!messageToRecall.getSenderId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền thu hồi tin nhắn này.");
        }

        messageToRecall.setMessageType(MessageType.RECALLED);
        messageToRecall.setContent("");

        messageDbMapper.updateById(messageToRecall);

        return convertToMessageResponse(messageToRecall);
    }

    @Override
    @Transactional
    public List<MessageResponse> getMessagesByConversation(UUID senderId, Long conversationId) {
        Conversation conversation = conversationDbMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("không tồn tại đoạn chat này");
        }

        if (!conversation.getUser1Id().equals(senderId) && !conversation.getUser2Id().equals(senderId)) {
            throw new SecurityException("Bạn không thuộc đoạn chat này!");
        }

        List<Message> messages = messageDbMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .orderByDesc(Message::getSentAt)
        );

        return messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse convertToMessageResponse(Message message) {
        if (message == null) return null;

        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());

        if (message.getMessageType() == MessageType.RECALLED) {
            response.setContent("");
        } else {
            response.setContent(message.getContent());
        }

        response.setMessageType(message.getMessageType().name());
        response.setSentAt(message.getSentAt());
        response.setSenderId(message.getSenderId());

        List<Reaction> reactions = reactionDbMapper.selectList(
                new LambdaQueryWrapper<Reaction>().eq(Reaction::getMessageId, message.getId())
        );
        if (reactions != null && !reactions.isEmpty()) {
            response.setReactions(
                    reactions.stream()
                            .map(this::convertToReactionResponse)
                            .collect(Collectors.toList())
            );
        } else {
            response.setReactions(Collections.emptyList());
        }

        return response;
    }

    @Override
    @Transactional
    public ReactionResponse addReaction(UUID currentUserId, CreateReactionRequest request) {
        Message message = messageDbMapper.selectById(request.getMessageId());
        if (message == null) {
            throw new RuntimeException("Không tìm thấy tin nhắn với ID: " + request.getMessageId());
        }

        Reaction newReaction = new Reaction();
        newReaction.setMessageId(message.getId());
        newReaction.setUserId(currentUserId);
        newReaction.setReactionType(ReactionType.valueOf(request.getReactionType().toUpperCase()));
        newReaction.setCreatedAt(Instant.now());

        reactionDbMapper.insert(newReaction);

        ReactionResponse response = convertToReactionResponse(newReaction);

        String destination = "/topic/conversation/" + message.getConversationId();
        WebSocketEvent<ReactionResponse> event = new WebSocketEvent<>("ADD_REACTION", response);
        messagingTemplate.convertAndSend(destination, event);

        return response;
    }

    private ReactionResponse convertToReactionResponse(Reaction reaction) {
        if (reaction == null) return null;
        ReactionResponse response = new ReactionResponse();
        response.setId(reaction.getId());
        response.setMessageId(reaction.getMessageId());
        response.setUserId(reaction.getUserId());
        response.setReactionType(reaction.getReactionType().name());
        return response;
    }

    @Override
    @Transactional
    public void removeReaction(Long reactionId, UUID userId) {
        Reaction reaction = reactionDbMapper.selectById(reactionId);
        if (reaction == null) {
            throw new RuntimeException("Reaction không tồn tại!");
        }
        if (!reaction.getUserId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xóa reaction này.");
        }

        Message message = messageDbMapper.selectById(reaction.getMessageId());
        Long conversationId = message != null ? message.getConversationId() : null;
        Long messageId = reaction.getMessageId();

        reactionDbMapper.deleteById(reactionId);

        if (conversationId != null) {
            String destination = "/topic/conversation/" + conversationId;
            Map<String, Long> payload = new HashMap<>();
            payload.put("messageId", messageId);
            payload.put("reactionId", reactionId);
            WebSocketEvent<Map<String, Long>> event = new WebSocketEvent<>("REMOVE_REACTION", payload);
            messagingTemplate.convertAndSend(destination, event);
        }
    }
}