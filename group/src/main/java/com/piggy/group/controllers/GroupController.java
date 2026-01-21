package com.piggy.group.controllers;

import com.piggy.group.dtos.AuthUser;
import com.piggy.group.dtos.requests.CreateGroupRequest;
import com.piggy.group.dtos.requests.UpdateGroupRequest;
import com.piggy.group.models.Group;
import com.piggy.group.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final AppService appService;

//    @PreAuthorize("isAuthenticated()")
//    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("")
    public ResponseEntity<Group> create(@Valid @RequestBody CreateGroupRequest request, @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(appService.createGroup(request, authUser.getUserId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Group> update(@PathVariable UUID id,@Valid @RequestBody UpdateGroupRequest request, @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.updateGroup(id,request, authUser.getUserId()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal AuthUser authUser){
        appService.deleteGroup(id, authUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
