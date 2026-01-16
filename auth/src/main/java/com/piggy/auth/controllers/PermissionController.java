package com.piggy.auth.controllers;

import com.piggy.auth.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("")
    public ResponseEntity<Void> create(@RequestBody String name){
        permissionService.create(name);
        return ResponseEntity.noContent().build();
    }
//    @PatchMapping("/{id}/add")
//    public ResponseEntity<Void> addPermission(@PathVariable Long id, @RequestBody Long permissionId){
//        roleService.addPermission(id, permissionId);
//        return ResponseEntity.noContent().build();
//    }
//    @PatchMapping("/{id}/remove")
//    public ResponseEntity<Void> removePermission(@PathVariable Long id, @RequestBody Long permissionId){
//        roleService.removePermission(id, permissionId);
//        return ResponseEntity.noContent().build();
//    }
}
