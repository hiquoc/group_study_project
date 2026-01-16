package com.piggy.chat.services;

import com.piggy.chat.dtos.requests.CreateGroupRequest;
import com.piggy.chat.dtos.requests.UpdateGroupRequest;
import com.piggy.chat.exceptions.NotFoundException;
import com.piggy.chat.models.Group;
import com.piggy.chat.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public Group create(CreateGroupRequest request,UUID userId){
        Group group=Group.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .avatarUrl(request.getAvatarUrl())
                .isPrivate(request.getIsPrivate())
                .createdBy(userId)
                .createdAt(Instant.now())
                .build();
        groupRepository.save(group);
        return group;
    }

    public Group update(Group group, UpdateGroupRequest request){
        if(request.getName()!=null && !Objects.equals(group.getName(), request.getName()))
            group.setName(request.getName());
        if(request.getAvatarUrl()!=null && !Objects.equals(group.getAvatarUrl(), request.getAvatarUrl()))
            group.setAvatarUrl(request.getAvatarUrl());
        if(request.getIsPrivate()!=null && !Objects.equals(group.getIsPrivate(), request.getIsPrivate())){
            group.setIsPrivate(request.getIsPrivate());
        }
        return group;
    }
    public void delete(UUID groupId){
        Group group=findById(groupId);
        groupRepository.delete(group);
    }

    public Group findById(UUID id){
        return groupRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Cant find group!"));
    }
}
