package com.piggy.chat.controllers;

import com.piggy.chat.dtos.requests.UpdateMemberRequest;
import com.piggy.chat.models.UserGroup;
import com.piggy.chat.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user-groups")
@RequiredArgsConstructor
public class UserGroupController {
    private final AppService appService;

    @PostMapping("")
    public ResponseEntity<UserGroup> addMember(@Valid @RequestBody UUID userId,
                                               Authentication authentication){
        return ResponseEntity.ok(appService.addMember(userId, UUID.fromString(authentication.getName())));
    }
    @PutMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<UserGroup> updateMember(@PathVariable UUID userId,
                                                @PathVariable UUID groupId,
                                                @Valid @RequestBody UpdateMemberRequest request,
                                                Authentication authentication){
        return ResponseEntity.ok(appService.updateMember(
                userId,groupId, request, UUID.fromString(authentication.getName())));
    }
    @DeleteMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId,
                                       @PathVariable UUID groupId,
                                       Authentication authentication){
        appService.removeMember(userId,groupId, UUID.fromString(authentication.getName()));
        return ResponseEntity.noContent().build();
    }
}
