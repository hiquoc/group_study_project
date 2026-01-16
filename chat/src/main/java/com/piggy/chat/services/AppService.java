package com.piggy.chat.services;

import com.piggy.chat.dtos.requests.CreateGroupRequest;
import com.piggy.chat.dtos.requests.UpdateGroupJoinRequest;
import com.piggy.chat.dtos.requests.UpdateGroupRequest;
import com.piggy.chat.dtos.requests.UpdateMemberRequest;
import com.piggy.chat.enums.GroupJoinRequestStatus;
import com.piggy.chat.enums.GroupJoinRequestType;
import com.piggy.chat.enums.GroupRole;
import com.piggy.chat.exceptions.BadRequestException;
import com.piggy.chat.exceptions.ForbiddenException;
import com.piggy.chat.exceptions.NotFoundException;
import com.piggy.chat.models.Group;
import com.piggy.chat.models.GroupJoinRequest;
import com.piggy.chat.models.InviteLink;
import com.piggy.chat.models.UserGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppService {
    private final GroupService groupService;
    private final GroupJoinRequestService groupJoinRequestService;
    private final UserGroupService userGroupService;
    private final InviteLinkService inviteLinkService;

    @Transactional
    public Group createGroup(CreateGroupRequest request, UUID userId) {
        Group group= groupService.create(request, userId);
        userGroupService.addOwner(userId,group);
        return group;
    }

    @Transactional
    public Group updateGroup(UUID groupId, UpdateGroupRequest request, UUID requesterId) {
        UserGroup requester = userGroupService.findByUserIdAndGroupId(requesterId, groupId);
        if (requester.getRole() != GroupRole.OWNER &&
                requester.getRole() != GroupRole.ADMIN) {
            throw new ForbiddenException("You dont have the permission to do this");
        }

        Group group = groupService.findById(groupId);
        boolean wasPrivate = group.getIsPrivate();
        groupService.update(group, request);

        if (request.getIsPrivate() != null && !wasPrivate && group.getIsPrivate()) {
            groupJoinRequestService.rejectPendingJoinRequests(groupId);
        }
        return group;
    }
    @Transactional
    public void deleteGroup(UUID groupId,UUID requesterId){
        UserGroup member= userGroupService.findByUserIdAndGroupId(requesterId,groupId);
        if(member.getRole()!= GroupRole.OWNER && member.getRole()!=GroupRole.ADMIN)
            throw new ForbiddenException("You dont have the permission to do this");
        groupService.delete(groupId);
    }


    //UserGroup
    @Transactional
    public UserGroup addMember(UUID userId, UUID groupId) {
        Group group=groupService.findById(groupId);
        return userGroupService.addMember(userId,group);
    }

    @Transactional
    public UserGroup updateMember(UUID userId, UUID groupId, UpdateMemberRequest request, UUID requesterId){
        UserGroup requester = userGroupService.findByUserIdAndGroupId(requesterId, groupId);
        UserGroup member = userGroupService.findByUserIdAndGroupId(userId, groupId);

        if (requester.getRole() != GroupRole.OWNER && requester.getRole() != GroupRole.ADMIN)
            throw new ForbiddenException("You don't have permission to change roles");

        if (requesterId.equals(userId))
            throw new BadRequestException("You cannot change your own role");

        if (member.getRole() == GroupRole.OWNER || request.getNewRole() == GroupRole.OWNER)
            throw new ForbiddenException("Owner role cannot be changed");

        if (requester.getRole() == GroupRole.ADMIN && member.getRole() == GroupRole.ADMIN)
            throw new ForbiddenException("Admin cannot modify another admin");

        if (requester.getRole() == GroupRole.ADMIN && request.getNewRole() == GroupRole.ADMIN)
            throw new ForbiddenException("Admin cannot promote members to admin");

        if (member.getRole() == request.getNewRole()) {
            throw new BadRequestException("Member already has this role");
        }
        return userGroupService.updateMember(member,request.getNewRole());
    }
    @Transactional
    public void removeMember(UUID userId, UUID groupId, UUID requesterId) {
        UserGroup requester = userGroupService.findByUserIdAndGroupId(requesterId, groupId);
        UserGroup target = userGroupService.findByUserIdAndGroupId(userId, groupId);

        GroupRole requesterRole = requester.getRole();
        GroupRole targetRole = target.getRole();

        if (targetRole == GroupRole.OWNER) {
            throw new ForbiddenException("Cannot remove group owner");
        }
        if(requesterRole!=GroupRole.OWNER && requesterRole!=GroupRole.ADMIN){
            throw new ForbiddenException("You dont have the permission to do this");
        }

        if(requesterRole==GroupRole.ADMIN) {
            if (targetRole == GroupRole.ADMIN) {
                throw new ForbiddenException("Admin cannot remove other admins");
            }
        }
        userGroupService.removeMember(target);
    }

    /// GroupJoinRequest
    @Transactional
    public GroupJoinRequest createRequest(UUID userId, UUID groupId, GroupJoinRequestType type,UUID requesterId) {
        Group group=groupService.findById(groupId);
        Optional<GroupJoinRequest> optional =
                groupJoinRequestService.findByGroupIdAndUserIdAndType( groupId,userId,type);
        if (optional.isPresent()) {
            GroupJoinRequest existing = optional.get();
            GroupJoinRequestStatus status = existing.getStatus();

            if (status == GroupJoinRequestStatus.PENDING) {
                throw new BadRequestException(
                        type == GroupJoinRequestType.JOIN_REQUEST
                                ? "User request to join is being reviewed"
                                : "Invitation already sent"
                );
            }

            if (status == GroupJoinRequestStatus.REJECTED) {
                throw new BadRequestException(
                        type == GroupJoinRequestType.JOIN_REQUEST
                                ? "User has been rejected"
                                : "User has rejected invitation"
                );
            }

            throw new BadRequestException("Request status is " + status);
        }
        return groupJoinRequestService.createRequest(userId, group, requesterId, type);
    }
    @Transactional
    public GroupJoinRequest reviewRequest(Long id, UpdateGroupJoinRequest updateRequest, UUID reviewerId) {
        GroupJoinRequest request = groupJoinRequestService.findById(id);
        Group group = request.getGroup();
        GroupJoinRequestStatus newStatus = updateRequest.getStatus();

        if (newStatus == GroupJoinRequestStatus.PENDING) {
            throw new BadRequestException("Cannot change status back to PENDING");
        }

        if (request.getStatus() == GroupJoinRequestStatus.APPROVED) {
            throw new BadRequestException("Approved request cannot be updated");
        }
        if (request.getType() == GroupJoinRequestType.JOIN_REQUEST) {
            UserGroup reviewer = userGroupService.findByUserIdAndGroupId(reviewerId, group.getId());

            if (reviewer.getRole() != GroupRole.ADMIN
                    && reviewer.getRole() != GroupRole.OWNER) {
                throw new ForbiddenException("Only admin or owner can review join requests");
            }
        }

        if (request.getType() == GroupJoinRequestType.INVITATION) {
            if (!request.getUserId().equals(reviewerId)) {
                throw new ForbiddenException("This is not your invitation request");
            }

            if (userGroupService.existsByUserIdAndGroupId(reviewerId, group.getId())) {
                throw new ForbiddenException("You already belong to this group");
            }
        }

        GroupJoinRequest updated = groupJoinRequestService.updateRequest(request, newStatus, reviewerId);

        if (newStatus == GroupJoinRequestStatus.APPROVED) {
            userGroupService.addMember(request.getUserId(), group);
        }

        return updated;
    }

    //InviteLink
    @Transactional
    public String createLink(UUID groupId, UUID userId) {
        Group group = groupService.findById(groupId);
        if (!userGroupService.existsByUserIdAndGroupId(userId, groupId)) {
            throw new ForbiddenException("You dont belong to this group!");
        }

        return inviteLinkService.create(group,userId);
    }
    @Transactional
    public void useLink(String code, UUID userId) {
        InviteLink inviteLink= inviteLinkService.useLink(code);
        userGroupService.addMember(userId, inviteLink.getGroup());
    }
}
