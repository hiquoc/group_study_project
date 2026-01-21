package com.piggy.auth.controllers;


import com.piggy.auth.dtos.AuthUser;
import com.piggy.auth.dtos.requests.ChangePasswordRequest;
import com.piggy.auth.dtos.requests.LoginRequest;
import com.piggy.auth.dtos.requests.RegisterRequest;
import com.piggy.auth.services.AccountService;
import com.piggy.auth.services.AppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AppService appService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(appService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        appService.register(request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, @AuthenticationPrincipal AuthUser authUser){
        appService.changePassword(authUser.getUserId(),request);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        appService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
