package com.piggy.chat.services;

import com.piggy.chat.dtos.requests.UpdateMemberRequest;
import com.piggy.chat.enums.GroupRole;
import com.piggy.chat.exceptions.BadRequestException;
import com.piggy.chat.exceptions.ForbiddenException;
import com.piggy.chat.exceptions.NotFoundException;
import com.piggy.chat.models.Group;
import com.piggy.chat.models.UserGroup;
import com.piggy.chat.models.UserGroupId;
import com.piggy.chat.repositories.UserGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGroupService {
    private final UserGroupRepository userGroupRepository;

    public UserGroup addMember(UUID userId, Group group) {
        if (existsByUserIdAndGroupId(userId, group.getId()))
            throw new BadRequestException("User already in this this group");
        return userGroupRepository.save(
                new UserGroup(
                        new UserGroupId(userId, group.getId()),
                        group,
                        GroupRole.MEMBER,
                        Instant.now()
                )
        );
    }

    public UserGroup updateMember(UserGroup member,GroupRole newRole) {
        member.setRole(newRole);
        return userGroupRepository.save(member);
    }


    public void removeMember(UserGroup target) {
        userGroupRepository.delete(target);
    }

    public void addOwner(UUID userId, Group group) {
        userGroupRepository.save(
                new UserGroup(
                        new UserGroupId(userId, group.getId()),
                        group,
                        GroupRole.OWNER,
                        Instant.now()
                )
        );
    }

    public UserGroup findByUserIdAndGroupId(UUID userId, UUID groupId) {
        return userGroupRepository.findByIdUserIdAndIdGroupId(userId, groupId)
                .orElseThrow(() -> new NotFoundException("User is not in this group"));
    }

    public boolean existsByUserIdAndGroupId(UUID userId,UUID groupId){
        return userGroupRepository.existsByIdUserIdAndIdGroupId(userId,groupId);
    }
}
