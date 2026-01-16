package com.piggy.auth.controllers;

import com.piggy.auth.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("")
    public ResponseEntity<Void> create(@RequestBody String name){
        roleService.create(name);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/add")
    public ResponseEntity<Void> addPermission(@PathVariable Long id, @RequestBody Long permissionId){
        roleService.addPermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/remove")
    public ResponseEntity<Void> removePermission(@PathVariable Long id, @RequestBody Long permissionId){
        roleService.removePermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }
}
