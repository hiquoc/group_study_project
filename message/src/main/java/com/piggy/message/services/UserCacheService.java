package com.piggy.message.services;

import com.piggy.message.models.UserCache;
import com.piggy.message.repositories.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserCacheRepository userCacheRepository;
    private final MongoTemplate mongoTemplate;

    public void updateUserCache(UUID userId, String username, String avatarUrl) {
        Query query = new Query(
                Criteria.where("_userId").is(userId)
        );

        Update update = new Update()
                .set("username", username)
                .set("avatarUrl", avatarUrl);

        mongoTemplate.upsert(query, update, UserCache.class);
    }

    public Map<UUID, UserCache> getUserCacheMap(Set<UUID> userIds) {
        return userCacheRepository.findByUserIdIn(userIds).stream().collect(
                Collectors.toMap(UserCache::getUserId,
                        userCache -> userCache));
    }
}
