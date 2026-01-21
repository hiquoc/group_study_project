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

    public Role create(String name){
        Role role=Role.builder().name(name).build();
        return roleRepository.save(role);
    }
    public void delete(Long roleId){
        roleRepository.deleteById(roleId);
    }

    public Role findById(Long roleId){
        return roleRepository.findById(roleId).orElseThrow(()->new NotFoundException("Role not found"));
    }

    public boolean checkIfExistsByPermissionId(Long permissionId) {
        return  roleRepository.existsByPermissions_Id(permissionId);
    }
}
