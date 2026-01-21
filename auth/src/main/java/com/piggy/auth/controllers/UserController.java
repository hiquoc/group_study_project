package com.piggy.auth.controllers;

import com.piggy.auth.dtos.AuthUser;
import com.piggy.auth.dtos.requests.UpdateUserRequest;
import com.piggy.auth.models.User;
import com.piggy.auth.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AppService appService;

    @GetMapping
    public ResponseEntity<User> getUser(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(appService.getUser(authUser.getUserId()));
    }
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody UpdateUserRequest request, @AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(appService.updateUser(request, authUser.getUserId()));
    }
}
