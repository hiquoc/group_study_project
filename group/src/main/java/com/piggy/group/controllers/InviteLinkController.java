package com.piggy.group.controllers;

import com.piggy.group.dtos.AuthUser;
import com.piggy.group.dtos.requests.CreateInviteLinkRequest;
import com.piggy.group.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invite-link")
@RequiredArgsConstructor
public class InviteLinkController {
    private final AppService appService;

    @PostMapping
    public String createInviteLink(
            @Valid @RequestBody CreateInviteLinkRequest request,
            @AuthenticationPrincipal AuthUser authUser) {
        return appService.createLink(request.getGroupId(), authUser.getUserId());
    }
    @PostMapping("/{code}")
    public ResponseEntity<Void> updateInvitationRequest(@PathVariable("code") String code,@AuthenticationPrincipal AuthUser authUser){
        appService.useLink(code, authUser.getUserId());
        return ResponseEntity.noContent().build();
    }
}
