package com.piggy.auth.repositories;

import com.piggy.auth.models.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRoleRepository extends JpaRepository<AccountRole, UUID> {
}
