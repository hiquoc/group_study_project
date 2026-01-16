package com.piggy.auth.services;

import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.Permission;
import com.piggy.auth.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public void create(String name){
        Permission permission =Permission.builder().name(name).build();
        permissionRepository.save(permission);
    }

    public Permission findById(Long roleId){
        return permissionRepository.findById(roleId).orElseThrow(()->new NotFoundException("Permission not found"));
    }
}
