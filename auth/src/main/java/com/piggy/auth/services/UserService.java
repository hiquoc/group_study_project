package com.piggy.auth.services;

import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.User;
import com.piggy.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(){
        User user=User.builder()
                .id(UUID.randomUUID())
                .build();
        return userRepository.save(user);
    }

    public void delete(UUID id){
        userRepository.deleteById(id);
    }

    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Can't find user!"));
    }
}
