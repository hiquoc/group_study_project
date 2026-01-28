package com.piggy.message.dtos.requests;

import com.piggy.message.dtos.others.MembersData;
import com.piggy.message.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateConversationRequest {
    private ConversationType type;
    private UUID groupId;
    private String displayName;
    private String displayAvatar;
    private List<MembersData> membersDataList;
}
