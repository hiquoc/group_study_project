package com.piggy.message.controllers;

import com.piggy.message.dtos.AuthUser;
import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.models.UserConversationInbox;
import com.piggy.message.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inbox")
@RequiredArgsConstructor
public class UserConversationInboxController {
    private final AppService appService;

    //Lay danh sach doan chat gan nhat
    @GetMapping("")
    public ResponseEntity<List<UserConversationInbox>>
        getRecentConversations(@AuthenticationPrincipal AuthUser authUser,
                           @RequestParam(name = "cursorTimestamp", required = false) Instant cursorTimestamp,
                           @RequestParam(name = "cursorConversationId", required = false) String cursorConversationId,
                           @RequestParam(name = "limit", required = false) Integer limit) {
            return ResponseEntity.ok(appService.getRecentConversations(authUser.getUserId(),
                    cursorTimestamp, cursorConversationId, limit==null?10:limit));
    }

}
