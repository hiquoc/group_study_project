package com.piggy.message.services;

import com.piggy.message.exceptions.NotFoundException;
import com.piggy.message.models.Message;
import com.piggy.message.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessages(String conversationId, Instant cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.findByConversationIdAndCreatedAtLessThan(conversationId,cursor,pageable);
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
//        messageRepository.save(message);
        return message;
    }
    public void deleteMessage(String messageId){
        Message message=getMessage(messageId);
        message.setDeleted(true);
//        messageRepository.save(message);
    }
}
