package com.axolotl.jobmatcher.controller;

// controller/AuthController.java
import com.axolotl.jobmatcher.dto.RegisterRequest;
import com.axolotl.jobmatcher.dto.user.LoginRequest;
import com.axolotl.jobmatcher.dto.user.LoginResponse;
import com.axolotl.jobmatcher.dto.user.UserResponse;
import com.axolotl.jobmatcher.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
