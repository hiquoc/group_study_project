package com.piggy.auth.services;


import com.piggy.auth.exceptions.NotFoundException;
import com.piggy.auth.models.Permission;
import com.piggy.auth.models.Role;
import com.piggy.auth.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public void create(String name){
        Role role=Role.builder().name(name).build();
        roleRepository.save(role);
    }
    @Transactional
    public void addPermission(Long roleId,Long permissionId){
        Role role=findById(roleId);
        Permission permission=permissionService.findById(permissionId);
        Set<Permission> permissionSet=role.getPermissions();
        permissionSet.add(permission);
    }
    @Transactional
    public void removePermission(Long roleId,Long permissionId){
        Role role=findById(roleId);
        Permission permission=permissionService.findById(permissionId);
        Set<Permission> permissionSet=role.getPermissions();
        permissionSet.remove(permission);
    }

    public Role findById(Long roleId){
        return roleRepository.findById(roleId).orElseThrow(()->new NotFoundException("Role not found"));
    }
}
