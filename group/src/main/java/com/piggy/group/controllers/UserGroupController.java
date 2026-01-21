package com.piggy.group.controllers;

import com.piggy.group.dtos.AuthUser;
import com.piggy.group.dtos.requests.UpdateMemberRequest;
import com.piggy.group.models.UserGroup;
import com.piggy.group.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("")
//@RequestMapping("/user-groups")
@RequiredArgsConstructor
public class UserGroupController {
    private final AppService appService;

//    @PostMapping("")
//    public ResponseEntity<UserGroup> addMember(@Valid @RequestBody UUID userId,
//                                               @AuthenticationPrincipal AuthUser authUser){
//        return ResponseEntity.ok(appService.addMember(userId, authUser.getUserId()));
//    }
    @PutMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<UserGroup> updateMember(@PathVariable("userId") UUID userId,
                                                @PathVariable("groupId") UUID groupId,
                                                @Valid @RequestBody UpdateMemberRequest request,
                                                  @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.updateMember(
                userId,groupId, request, authUser.getUserId()));
    }
    @DeleteMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId,
                                       @PathVariable UUID groupId,
                                       @AuthenticationPrincipal AuthUser authUser){
        appService.removeMember(userId,groupId, authUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
