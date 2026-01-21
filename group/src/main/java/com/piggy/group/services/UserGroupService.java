package com.piggy.group.services;

import com.piggy.group.enums.GroupRole;
import com.piggy.group.exceptions.BadRequestException;
import com.piggy.group.exceptions.NotFoundException;
import com.piggy.group.models.Group;
import com.piggy.group.models.UserGroup;
import com.piggy.group.models.UserGroupId;
import com.piggy.group.repositories.UserGroupRepository;
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
        UserGroup userGroup = UserGroup.builder()
                .userId(userId)
                .group(group)
                .role(GroupRole.MEMBER)
                .build();
        return userGroupRepository.save(userGroup);
    }

    public UserGroup updateMember(UserGroup member,GroupRole newRole) {
        member.setRole(newRole);
        return userGroupRepository.save(member);
    }


    public void removeMember(UserGroup target) {
        userGroupRepository.delete(target);
    }

    public void addOwner(UUID userId, Group group) {
        UserGroup userGroup = UserGroup.builder()
                .userId(userId)
                .group(group)
                .role(GroupRole.OWNER)
                .build();
        userGroupRepository.save(userGroup);
    }

    public UserGroup findByUserIdAndGroupId(UUID userId, UUID groupId) {
        return userGroupRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new NotFoundException("User is not in this group"));
    }

    public boolean existsByUserIdAndGroupId(UUID userId,UUID groupId){
        return userGroupRepository.existsByUserIdAndGroupId(userId,groupId);
    }
}
