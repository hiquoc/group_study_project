package com.piggy.group.controllers;

import com.piggy.group.dtos.AuthUser;
import com.piggy.group.dtos.requests.CreateGroupJoinRequest;
import com.piggy.group.dtos.requests.UpdateGroupJoinRequest;
import com.piggy.group.enums.GroupJoinRequestType;
import com.piggy.group.models.GroupJoinRequest;
import com.piggy.group.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class GroupJoinRequestController {
    private final AppService appService;

    @PostMapping("/invite")
    public ResponseEntity<GroupJoinRequest> inviteRequest(@Valid @RequestBody CreateGroupJoinRequest request, @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.createRequest(request.getUserId(),request.getGroupId(), GroupJoinRequestType.INVITATION, authUser.getUserId()));
    }
    @PostMapping("/join")
    public ResponseEntity<GroupJoinRequest> joinRequest(@Valid @RequestBody CreateGroupJoinRequest request, @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.createRequest(request.getUserId(),request.getGroupId(),GroupJoinRequestType.JOIN_REQUEST, authUser.getUserId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<GroupJoinRequest> reviewRequest(@PathVariable("id") Long id, @Valid @RequestBody UpdateGroupJoinRequest request, @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.reviewRequest(id,request, authUser.getUserId()));
    }


}
