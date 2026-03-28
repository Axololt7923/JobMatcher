package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.job.JobRequest;
import com.axolotl.jobmatcher.dto.job.JobResponse;
import com.axolotl.jobmatcher.service.JobService;
import com.axolotl.jobmatcher.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
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

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllActivate(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "10" ) int limit,
            @RequestParam(defaultValue = "0") int offset) {
        if (title != null) {
            return ResponseEntity.ok(jobService.search(title));
        }
        return ResponseEntity.ok(jobService.getAllActivate(limit, offset));
    }

    @GetMapping({"/all"})
    public ResponseEntity<List<JobResponse>> getAll(
            @RequestParam(required = false) String title) {
        if (title != null) {
            return ResponseEntity.ok(jobService.search(title));
        }
        return ResponseEntity.ok(jobService.getAll());
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