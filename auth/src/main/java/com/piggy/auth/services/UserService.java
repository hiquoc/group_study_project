package com.piggy.auth.services;

import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.User;
import com.piggy.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;

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

    public User update(UUID id, String name,String email,String phone,String avatarUrl){
        User user=findById(id);
        updateIfPresent(name, user.getName(), user::setName);
        updateIfPresent(email, user.getEmail(), user::setEmail);
        updateIfPresent(phone, user.getPhone(), user::setPhone);
        updateIfPresent(avatarUrl, user.getAvatarUrl(), user::setAvatarUrl);
        return user;
    }
    public void delete(UUID id){
        userRepository.deleteById(id);
    }

    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Can't find user!"));
    }
    private void updateIfPresent(String newValue, String oldValue, Consumer<String> setter) {
        if (newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) {
            setter.accept(newValue);
        }
    }

}
