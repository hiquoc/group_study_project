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

    public Permission create(String name){
        Permission permission =Permission.builder().name(name).build();
        return permissionRepository.save(permission);
    }

    public Permission update(Long permissionId,String name,String description) {
        Permission permission = findById(permissionId);
        if(name!=null && !name.isEmpty() && !permission.getName().equals(name)){
            permission.setName(name);
        }
        if(description!=null && !description.isEmpty() && !permission.getDescription().equals(description)){
            permission.setDescription(description);
        }
        return permission;
    }

    public void delete(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    public Permission findById(Long id){
        return permissionRepository.findById(id).orElseThrow(()->new NotFoundException("Permission not found"));
    }


}
