package com.piggy.group.repositories;

import com.piggy.group.models.UserGroup;
import com.piggy.group.models.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
    Optional<UserGroup> findByUserIdAndGroupId(UUID userId, UUID groupId);
    boolean existsByUserIdAndGroupId(UUID userId, UUID groupId);

}
