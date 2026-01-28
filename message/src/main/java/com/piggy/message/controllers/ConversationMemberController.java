package com.piggy.message.controllers;

import com.piggy.message.dtos.AuthUser;
import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.dtos.requests.CreateConversationRequest;
import com.piggy.message.models.Conversation;
import com.piggy.message.models.ConversationMember;
import com.piggy.message.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversation-member")
@RequiredArgsConstructor
public class ConversationMemberController {
    private final AppService appService;

    //Duoc goi co thanh vien moi vao group
    @PostMapping("/{groupId}")
    public ResponseEntity<Void> addConversationMember(@PathVariable("groupId") UUID groupId,
                                                                    @RequestBody MembersData membersData){
        appService.addConversationMemberWhenNewUserEnterGroup(groupId,membersData);
        return ResponseEntity.noContent().build();
    }
    //Duoc goi co thanh vien roi khoi group
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroupConversation(@PathVariable("groupId") UUID groupId,
                                                        @RequestBody UUID userId){
        appService.removeConversationMemberWhenUserLeaveGroup(groupId,userId);
        return ResponseEntity.ok().build();
    }

}
