package com.axolotl.jobmatcher.controller;

import com.axolotl.jobmatcher.dto.company.CompanyRequest;
import com.axolotl.jobmatcher.dto.company.CompanyResponse;
import com.axolotl.jobmatcher.security.UserPrincipal;
import com.axolotl.jobmatcher.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> company(
            @Valid @RequestBody CompanyRequest request
            ,@AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(companyService.create(request, principal.getId()));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> companies(
            @RequestParam(required = false) UUID id) {
        if (id != null) {
            return ResponseEntity.ok(companyService.getById(id));
        }
        return ResponseEntity.ok(companyService.getAll());
    }

    @PutMapping("/update")
    public ResponseEntity<CompanyResponse> update(
            @Valid @RequestBody CompanyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(companyService.update(request, principal.getId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal) {
        companyService.delete( principal.getId());
        return ResponseEntity.noContent().build();
    }
}