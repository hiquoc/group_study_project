package com.piggy.chat.utils;

import com.piggy.chat.repositories.InviteLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final InviteLinkRepository inviteLinkRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void deleteExpiredInviteLink(){
        inviteLinkRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
