package com.piggy.message.controllers;

import com.piggy.message.dtos.AuthUser;
import com.piggy.message.dtos.requests.CreateConversationRequest;
import com.piggy.message.models.Conversation;
import com.piggy.message.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final AppService appService;

    //Duoc goi khi tao group
    @PostMapping("")
    public ResponseEntity<Conversation> createConversation(@RequestBody CreateConversationRequest request){
        return ResponseEntity.ok(appService.createConversation(request.getType(),request.getGroupId(),
                request.getDisplayName(),request.getDisplayAvatar(),request.getMembersDataList()));
    }
    //Duoc goi khi xoa group
    @DeleteMapping("/group")
    public ResponseEntity<Void> deleteGroupConversation(@RequestParam("groupId") UUID groupId){
        appService.deleteGroupConversation(groupId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/private")
    public ResponseEntity<Void> deletePrivateConversation( @AuthenticationPrincipal AuthUser authUser,
                                                           @RequestParam("userId") UUID userId){
        appService.deletePrivateConversation(authUser.getUserId(),userId);
        return ResponseEntity.ok().build();
    }
}
