package com.piggy.chat.services;

import com.piggy.chat.enums.GroupJoinRequestStatus;
import com.piggy.chat.enums.GroupJoinRequestType;
import com.piggy.chat.exceptions.NotFoundException;
import com.piggy.chat.models.Group;
import com.piggy.chat.models.GroupJoinRequest;
import com.piggy.chat.repositories.GroupJoinRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupJoinRequestService {
    private final GroupJoinRequestRepository groupJoinRequestRepository;

    public GroupJoinRequest createRequest(UUID userId, Group group,UUID requesterId, GroupJoinRequestType type) {
        GroupJoinRequest request = GroupJoinRequest.builder()
                .userId(userId)
                .group(group)
                .requestedBy(requesterId)
                .type(type)
                .status(GroupJoinRequestStatus.PENDING)
                .build();
        return groupJoinRequestRepository.save(request);
    }


    public GroupJoinRequest updateRequest(GroupJoinRequest request,
                                              GroupJoinRequestStatus status,
                                              UUID userId) {
        if (status == GroupJoinRequestStatus.APPROVED) {
            approveRequest(request, userId);
        } else if (status == GroupJoinRequestStatus.REJECTED){
            rejectRequest(request, userId);
        }
        return request;
    }

    private void approveRequest(GroupJoinRequest request, UUID userId) {
        request.setStatus(GroupJoinRequestStatus.APPROVED);
        request.setReviewedBy(userId);
        request.setReviewedAt(Instant.now());
//        groupJoinRequestRepository.save(request);
    }

    private void rejectRequest(GroupJoinRequest request, UUID userId) {
        request.setStatus(GroupJoinRequestStatus.REJECTED);
        request.setReviewedBy(userId);
        request.setReviewedAt(Instant.now());
//        groupJoinRequestRepository.save(request);
    }

    public GroupJoinRequest findById(Long id) {
        return groupJoinRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cant find request"));
    }

    public void rejectPendingJoinRequests(UUID groupId) {
        groupJoinRequestRepository.rejectPendingJoinRequests(groupId);
    }
    public Optional<GroupJoinRequest> findByGroupIdAndUserIdAndType(UUID groupId,UUID userId,GroupJoinRequestType type){
        return groupJoinRequestRepository.findByGroupIdAndUserIdAndType( groupId,userId,GroupJoinRequestType.JOIN_REQUEST);
    }
}
