package com.piggy.auth.controllers;


import com.piggy.auth.dtos.requests.LoginRequest;
import com.piggy.auth.dtos.requests.RegisterRequest;
import com.piggy.auth.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(accountService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        accountService.register(request);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
