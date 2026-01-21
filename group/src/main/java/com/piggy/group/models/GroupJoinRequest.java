package com.piggy.group.models;

import com.piggy.group.enums.GroupJoinRequestStatus;
import com.piggy.group.enums.GroupJoinRequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "group_join_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id", "type"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GroupJoinRequest {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Column(name = "requested_by")
    private UUID requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GroupJoinRequestType type; //JOIN_REQUEST, INVITATION

    @Enumerated(EnumType.STRING)
    private GroupJoinRequestStatus status; // PENDING, APPROVED, REJECTED

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}
