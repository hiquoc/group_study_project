package com.piggy.message.services.others;

import com.piggy.message.dtos.responses.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/batch")
    List<UserProfileResponse> getUsersProfile(@RequestBody List<UUID> ids);
}
