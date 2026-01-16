package com.piggy.chat.controllers;

import com.piggy.chat.dtos.requests.CreateGroupJoinRequest;
import com.piggy.chat.dtos.requests.UpdateGroupJoinRequest;
import com.piggy.chat.enums.GroupJoinRequestType;
import com.piggy.chat.models.GroupJoinRequest;
import com.piggy.chat.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class GroupJoinRequestController {
    private final AppService appService;

    @PostMapping("/invite")
    public ResponseEntity<GroupJoinRequest> inviteRequest(@Valid @RequestBody CreateGroupJoinRequest request, Authentication authentication){
        return ResponseEntity.ok(appService.createRequest(request.getUserId(),request.getGroupId(), GroupJoinRequestType.INVITATION, UUID.fromString(authentication.getName())));
    }
    @PostMapping("/join")
    public ResponseEntity<GroupJoinRequest> joinRequest(@Valid @RequestBody CreateGroupJoinRequest request, Authentication authentication){
        return ResponseEntity.ok(appService.createRequest(request.getUserId(),request.getGroupId(),GroupJoinRequestType.JOIN_REQUEST, UUID.fromString(authentication.getName())));
    }
    @PutMapping("/{id}")
    public ResponseEntity<GroupJoinRequest> reviewRequest(@PathVariable Long id, @Valid @RequestBody UpdateGroupJoinRequest request, Authentication authentication){
        return ResponseEntity.ok(appService.reviewRequest(id,request, UUID.fromString(authentication.getName())));
    }


}
