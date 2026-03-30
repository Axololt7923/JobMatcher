package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.application.ApplicationRequest;
import com.axolotl.jobmatcher.dto.application.ApplicationResponse;
import com.axolotl.jobmatcher.entity.Application;
import com.axolotl.jobmatcher.service.ApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Deprecated
@RestController
@RequestMapping("/api/applications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable UUID userId,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.apply(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationResponse>> getByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(applicationService.getByUserId(userId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getByJobId(
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(applicationService.getByJobId(jobId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam Application.Status status) {
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }
}