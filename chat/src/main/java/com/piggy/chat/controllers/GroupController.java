package com.piggy.chat.controllers;

import com.piggy.chat.dtos.requests.CreateGroupRequest;
import com.piggy.chat.dtos.requests.UpdateGroupRequest;
import com.piggy.chat.models.Group;
import com.piggy.chat.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<Group> create(@Valid @RequestBody CreateGroupRequest request, Authentication authentication){
        return ResponseEntity.ok(appService.createGroup(request, UUID.fromString(authentication.getName())));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Group> update(@PathVariable UUID id,@Valid @RequestBody UpdateGroupRequest request, Authentication authentication){
        return ResponseEntity.ok(appService.updateGroup(id,request, UUID.fromString(authentication.getName())));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication){
        appService.deleteGroup(id, UUID.fromString(authentication.getName()));
        return ResponseEntity.noContent().build();
    }
}
