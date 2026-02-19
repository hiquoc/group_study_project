package com.piggy.message.repositories;

import com.piggy.message.models.UserCache;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UserCacheRepository extends MongoRepository<UserCache, String> {
    List<UserCache> findByUserIdIn(Set<UUID> userIds);
}
