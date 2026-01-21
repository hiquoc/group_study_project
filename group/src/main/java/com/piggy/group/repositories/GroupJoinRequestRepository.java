package com.piggy.group.repositories;

import com.piggy.group.enums.GroupJoinRequestType;
import com.piggy.group.models.GroupJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest,Long> {

    Optional<GroupJoinRequest> findByGroupIdAndUserIdAndType(UUID groupId, UUID userId, GroupJoinRequestType type);
    @Modifying
    @Transactional
    @Query("""
        UPDATE GroupJoinRequest g
        SET g.status='REJECTED'
        WHERE g.group.id=:groupId
            AND g.status='PENDING'
            AND g.type='JOIN_REQUEST'
        """)
    void rejectPendingJoinRequests(UUID groupId);
}
