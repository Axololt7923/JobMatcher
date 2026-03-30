package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.cv.CVResponse;
import com.axolotl.jobmatcher.security.UserPrincipal;
import com.axolotl.jobmatcher.service.CVService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cvs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CVController {

    private final CVService cvService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CVResponse> upload(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        return ResponseEntity.ok(cvService.upload(principal.getId(), file));
    }

    @GetMapping
    public ResponseEntity<List<CVResponse>> getByUserId(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(cvService.getByUserId(principal.getId()));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        cvService.delete(id, principal.getId());
        return ResponseEntity.ok("CV " + id + " has been deleted");
    }
}