package com.piggy.group.services;

import com.piggy.group.exceptions.NotFoundException;
import com.piggy.group.models.Group;
import com.piggy.group.models.InviteLink;
import com.piggy.group.repositories.InviteLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteLinkService {

    private final InviteLinkRepository inviteLinkRepository;

    private final Duration EXPIRE_DURATION = Duration.ofDays(1);
    @Value("${web-url}")
    private String webUrl;

    public String create(Group group, UUID userId) {
        Instant now = Instant.now();

        Optional<InviteLink> existingLink =
                inviteLinkRepository.findFirstByGroupIdAndCreatedByAndExpiresAtAfter(group.getId(), userId, now);

        if (existingLink.isPresent()) {
            return createLink(existingLink.get().getCode());
        }

        String code = generateSecureCode();

        InviteLink inviteLink = InviteLink.builder()
                .group(group)
                .createdBy(userId)
                .code(code)
                .expiresAt(now.plus(EXPIRE_DURATION))
                .build();

        inviteLinkRepository.save(inviteLink);

        return createLink(code);
    }

    public InviteLink useLink(String code) {
        InviteLink inviteLink = inviteLinkRepository.findFirstByCode(code)
                .orElseThrow(() -> new NotFoundException("Invite link expired or doesn't exist"));
        if (inviteLink.getExpiresAt().isBefore(Instant.now())) {
            throw new NotFoundException("Invite link expired");
        }

        return inviteLink;
    }

    private String generateSecureCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[36];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String createLink(String code) {
        return webUrl + "/invite/" + code;
    }

}
