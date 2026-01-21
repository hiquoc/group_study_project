package com.piggy.group.repositories;

import com.piggy.group.models.InviteLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface InviteLinkRepository extends JpaRepository<InviteLink, UUID> {
    Optional<InviteLink> findFirstByGroupIdAndCreatedByAndExpiresAtAfter(
            UUID groupId,
            UUID createdBy,
            Instant now
    );
    Optional<InviteLink> findFirstByCode(String code);
    void deleteByExpiresAtBefore(Instant now);
}
