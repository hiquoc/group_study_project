package com.piggy.message.controllers;

import com.piggy.message.dtos.AuthUser;
import com.piggy.message.dtos.requests.SendMessageRequest;
import com.piggy.message.models.Message;
import com.piggy.message.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final AppService appService;

    @PostMapping("/private")
    public ResponseEntity<Message> sendPrivateMessage(@Valid @RequestBody SendMessageRequest request,@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.sendPrivateMessage(authUser.getUserId(),request.getContent(),
                request.getConversationId(), request.getReceiverId()));
    }
    @PostMapping("/group")
    public ResponseEntity<Message> sendGroupMessage(@Valid @RequestBody SendMessageRequest request,@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.sendGroupMessage(authUser.getUserId(),request.getContent(),
                request.getConversationId()));
    }
}
