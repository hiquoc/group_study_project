package com.piggy.group.models;

import com.piggy.group.enums.GroupRole;
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
@Table(name = "user_groups",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}),
        indexes = {
                @Index(name = "idx_user_group_user", columnList = "user_id"),
                @Index(name = "idx_user_group_group", columnList = "group_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @CreationTimestamp
    private Instant joinedAt;
}

