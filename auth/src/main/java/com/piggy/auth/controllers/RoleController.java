package com.piggy.auth.controllers;

import com.piggy.auth.models.Role;
import com.piggy.auth.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final AppService appService;

    @PostMapping("")
    public ResponseEntity<Role> create(@RequestBody String name){
        return ResponseEntity.ok(appService.createRole(name));
    }
    @PatchMapping("/{id}/add")
    public ResponseEntity<Role> addPermission(@PathVariable Long id, @RequestBody Long permissionId){
        return ResponseEntity.ok(appService.addPermissionToRole(id, permissionId));
    }
    @PatchMapping("/{id}/remove")
    public ResponseEntity<Role> removePermission(@PathVariable Long id, @RequestBody Long permissionId){
        return ResponseEntity.ok(appService.removePermissionFromRole(id, permissionId));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        appService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
