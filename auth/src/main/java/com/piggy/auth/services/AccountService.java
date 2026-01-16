package com.piggy.auth.services;

import com.piggy.auth.dtos.requests.LoginRequest;
import com.piggy.auth.dtos.requests.RegisterRequest;
import com.piggy.auth.exceptions.BadRequestException;
import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.Account;
import com.piggy.auth.models.User;
import com.piggy.auth.repositories.AccountRepository;
import com.piggy.auth.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public String login( LoginRequest request) {
        Account account=getAccountByUsername(request.getUsername());
        if(!passwordEncoder.matches(request.getPassword(),account.getPassword()))
            throw new BadRequestException("Username or password is incorrect!");
        return jwtService.generateAccessToken(account.getId(),account.getRole().getName(),account.getUser().getId());
    }

    @Transactional
    public void register( RegisterRequest request) {
        if(accountRepository.existsByUsername(request.getUsername()))
            throw new BadRequestException("Username has existed!");
        String passwordHashed= passwordEncoder.encode(request.getPassword());
        User user=userService.create();
        Account account=Account.builder()
                .username(request.getUsername())
                .password(passwordHashed)
                .role(roleService.findById(1L))
                .status("ACTIVE")
                .user(user)
                .build();
        accountRepository.save(account);
    }
    public void delete(UUID id){
        accountRepository.deleteById(id);
    }

    ///////////////////
    private Account getAccountByUsername(String username){
        return accountRepository.findByUsername(username)
                .orElseThrow(()->new NotFoundException("Account not found with username!"));
    }
}
