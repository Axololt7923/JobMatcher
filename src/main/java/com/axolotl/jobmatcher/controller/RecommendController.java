package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.job.JobResponse;
import com.axolotl.jobmatcher.security.UserPrincipal;
import com.axolotl.jobmatcher.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> recommendJobs(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "10") int topK) {
        return ResponseEntity.ok(recommendService.recommendJobs(principal.getId(), topK));
    }
}