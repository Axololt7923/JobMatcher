package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.job.JobRequest;
import com.axolotl.jobmatcher.dto.job.JobResponse;
import com.axolotl.jobmatcher.service.JobService;
import com.axolotl.jobmatcher.security.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> create(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(jobService.create(request, principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getById(id));
    }

    @GetMapping({"/all"})
    public ResponseEntity<List<JobResponse>> job(
            @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "0") int offset) {
        return ResponseEntity.ok(jobService.getAll(isActive, limit, offset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok( jobService.update(id, principal.getId() , request) );
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<JobResponse> inactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        jobService.inactivate(id, principal.getId());
        return ResponseEntity.ok(jobService.getById(id));
    }
}