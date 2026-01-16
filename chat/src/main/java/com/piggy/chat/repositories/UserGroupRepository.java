package com.piggy.chat.repositories;

import com.piggy.chat.models.UserGroup;
import com.piggy.chat.models.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
    Optional<UserGroup> findByIdUserIdAndIdGroupId(UUID userId,UUID groupId);
    boolean existsByIdUserIdAndIdGroupId(UUID userId, UUID groupId);

}
