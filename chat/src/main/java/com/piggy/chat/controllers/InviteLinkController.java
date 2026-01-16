package com.piggy.chat.controllers;

import com.piggy.chat.dtos.requests.CreateInviteLinkRequest;
import com.piggy.chat.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication) {
        return appService.createLink(request.getGroupId(), UUID.fromString(authentication.getName()));
    }
    @PostMapping("/{code}")
    public ResponseEntity<Void> updateInvitationRequest(@PathVariable String code, Authentication authentication){
        appService.useLink(code, UUID.fromString(authentication.getName()));
        return ResponseEntity.noContent().build();
    }
}
