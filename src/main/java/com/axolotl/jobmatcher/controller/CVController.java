package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.cv.CVResponse;
import com.axolotl.jobmatcher.security.UserPrincipal;
import com.axolotl.jobmatcher.service.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cvs")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;

    @PostMapping
    public ResponseEntity<CVResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        return ResponseEntity.ok(cvService.upload(principal.getId(), file));
    }

    @GetMapping
    public ResponseEntity<List<CVResponse>> getByUserId(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(cvService.getByUserId(principal.getId()));
    }

}