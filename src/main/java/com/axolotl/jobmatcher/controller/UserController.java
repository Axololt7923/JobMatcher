package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.user.UserResponse;
import com.axolotl.jobmatcher.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<List<UserResponse>> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(userService.getById(id));
    }

}
