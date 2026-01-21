package com.piggy.auth.controllers;

import com.piggy.auth.dtos.requests.UpdatePermissionRequest;
import com.piggy.auth.models.Permission;
import com.piggy.auth.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final AppService appService;

    @PostMapping("")
    public ResponseEntity<Permission> create(@RequestBody String name){
        return ResponseEntity.ok(appService.createPermission(name));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Permission> update(@PathVariable Long id, @RequestBody UpdatePermissionRequest request){
        return ResponseEntity.ok(appService.updatePermission(id,request.getName(),request.getDescription()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        appService.deletePermission(id);
        return ResponseEntity.ok().build();
    }
}
