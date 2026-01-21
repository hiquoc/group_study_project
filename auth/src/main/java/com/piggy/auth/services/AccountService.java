package com.piggy.auth.services;

import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.Account;
import com.piggy.auth.models.Role;
import com.piggy.auth.models.User;
import com.piggy.auth.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(String username, String passwordHashed, Role role,User user){
        Account account=Account.builder()
                .username(username)
                .password(passwordHashed)
                .role(role)
                .status("ACTIVE")
                .user(user)
                .build();
        return accountRepository.save(account);
    }

    public void delete(UUID id){
        accountRepository.deleteById(id);
    }

    ///////////////////
    public Account getAccountByUsername(String username){
        return accountRepository.findByUsername(username)
                .orElseThrow(()->new NotFoundException("Account not found with username!"));
    }
    public boolean checkIfUsernameExists(String username){
        return accountRepository.existsByUsername(username);
    }

    public boolean checkIfExistsByRoleId(Long id) {
        return accountRepository.existsByRoleId(id);
    }
}
