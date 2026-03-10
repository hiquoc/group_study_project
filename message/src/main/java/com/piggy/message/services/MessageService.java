package com.piggy.message.services;

import com.piggy.message.dtos.responses.MessageResponse;
import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Message;
import com.piggy.message.models.UserCache;
import com.piggy.message.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<Message> getMessages(String conversationId,
                                     Instant cursor,
                                     int limit) {

        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        if (cursor == null || cursor.equals(Instant.EPOCH)) {
            return messageRepository
                    .findByConversationIdOrderByCreatedAtDesc(
                            conversationId,
                            pageable
                    );
        }

        return messageRepository
                .findByConversationIdAndCreatedAtLessThanOrderByCreatedAtDesc(
                        conversationId,
                        cursor,
                        pageable
                );
    }

    public Message getMessage(String messageId){
        return messageRepository.findById(messageId)
                .orElseThrow(()-> new NotFoundException("Message not found"));
    }
    public Message sendMessage(UUID senderId,String content, String conversationId){
        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(content)
                .build();
        return messageRepository.save(message);
    }

    public Message updateMessage(String messageId, String content){
        Message message=getMessage(messageId);
        message.setContent(content);

        return messageRepository.save(message);
    }
    public void deleteMessage(String messageId){
        Message message=getMessage(messageId);
        message.setDeleted(true);
        messageRepository.save(message);
    }
    public List<Message> findAllById(Set<String> messageIds){
        return messageRepository.findAllById(messageIds);
    }

}
