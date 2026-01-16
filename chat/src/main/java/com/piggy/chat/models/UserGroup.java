package com.piggy.chat.models;

import com.piggy.chat.enums.GroupRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_groups",
uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","group_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserGroup {
    @EmbeddedId
    private UserGroupId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupId")
    @JoinColumn(name = "group_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @CreationTimestamp
    @Column(name = "joined_at")
    private Instant joinedAt;

}
