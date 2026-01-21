package com.piggy.auth.repositories;

import com.piggy.auth.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);

    boolean existsByRoleId(Long id);
}
