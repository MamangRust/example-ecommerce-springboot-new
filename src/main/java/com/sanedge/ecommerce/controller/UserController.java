package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.user.CreateUserRequest;
import com.sanedge.ecommerce.domain.requests.user.FindAllUserRequest;
import com.sanedge.ecommerce.domain.requests.user.UpdateUserRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.user.UserResponse;
import com.sanedge.ecommerce.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.ecommerce.service.user.UserCommandService;
import com.sanedge.ecommerce.service.user.UserQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponse>>> findAll(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findAll(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userQueryService.findById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findByTrashed(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userCommandService.create(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(@RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> trash(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> restore(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(userCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(userCommandService.deleteAll());
    }
}