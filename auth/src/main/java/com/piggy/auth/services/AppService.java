package com.piggy.auth.services;

import com.piggy.auth.dtos.requests.ChangePasswordRequest;
import com.piggy.auth.dtos.requests.LoginRequest;
import com.piggy.auth.dtos.requests.RegisterRequest;
import com.piggy.auth.dtos.requests.UpdateUserRequest;
import com.piggy.auth.exceptions.BadRequestException;
import com.piggy.auth.models.Account;
import com.piggy.auth.models.Permission;
import com.piggy.auth.models.Role;
import com.piggy.auth.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppService {
    private final AccountService accountService;
    private final JwtService jwtService;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;

    //Account
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        Account account= accountService.getAccountByUsername(request.getUsername());
        if(!passwordEncoder.matches(request.getPassword(),account.getPassword()))
            throw new BadRequestException("Username or password is incorrect!");
        return jwtService.generateAccessToken(account.getId()
                ,account.getRole().getName(),account.getUser().getId());
    }
    @Transactional
    public void register( RegisterRequest request) {
        if(accountService.checkIfUsernameExists(request.getUsername()))
            throw new BadRequestException("Username has existed!");
        String passwordHashed= passwordEncoder.encode(request.getPassword());
        User user=userService.create();
        accountService.create(request.getUsername(), passwordHashed,
                roleService.findById(1L),user);
    }
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user=userService.findById(userId);
        Account account=user.getAccount();
        if(!passwordEncoder.matches(request.getOldPassword(),account.getPassword()))
            throw new BadRequestException("Old password is incorrect!");
        //ADD password validation
        if(!request.getNewPassword().equals(request.getOldPassword()))
            account.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
    @Transactional
    public void deleteAccount(UUID id){
        accountService.delete(id);
    }

    //Role
    public Role createRole(String name){
        return roleService.create(name);
    }
    @Transactional
    public void deleteRole(Long id){
        if(accountService.checkIfExistsByRoleId(id))
            throw new BadRequestException("Role has been used!");
        roleService.delete(id);
    }

    //Permission
    public Permission createPermission(String name){
        return permissionService.create(name);
    }
    @Transactional
    public Permission updatePermission(Long permissionId,String name,String description){
        return permissionService.update(permissionId,name,description);
    }
    @Transactional
    public void deletePermission(Long permissionId){
        if(roleService.checkIfExistsByPermissionId(permissionId))
            throw new BadRequestException("Permission is being used!");
        permissionService.delete(permissionId);
    }
    @Transactional
    public Role addPermissionToRole(Long roleId,Long permissionId){
        Role role=roleService.findById(roleId);
        Permission permission=permissionService.findById(permissionId);
        Set<Permission> permissionSet=role.getPermissions();
        permissionSet.add(permission);
        return role;
    }

    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId){
        Role role=roleService.findById(roleId);
        Permission permission=permissionService.findById(permissionId);
        Set<Permission> permissionSet=role.getPermissions();
        permissionSet.remove(permission);
        return role;
    }

    //User
    public User getUser(UUID id){
        return userService.findById(id);
    }

    @Transactional
    public User updateUser(UpdateUserRequest request,UUID userId) {
        return userService.update(userId,request.getName(),request.getEmail(),
                request.getPhone(), request.getAvatarUrl());
    }
}
