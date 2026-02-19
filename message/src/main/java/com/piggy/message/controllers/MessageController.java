package com.piggy.message.controllers;

import com.piggy.message.dtos.AuthUser;
import com.piggy.message.dtos.requests.SendMessageRequest;
import com.piggy.message.models.Message;
import com.piggy.message.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

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
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getMessages(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("conversationId") String conversationId,
            @RequestParam(value = "cursor", required = false) Instant cursor,
            @RequestParam(value = "limit", required = false) Integer limit) {

        return ResponseEntity.ok(appService.getMessages(authUser.getUserId(), conversationId, cursor, limit));
    }

}
