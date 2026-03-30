package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.company.CompanyRequest;
import com.axolotl.jobmatcher.dto.company.CompanyResponse;
import com.axolotl.jobmatcher.entity.Company;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.CompanyRepository;
import com.axolotl.jobmatcher.repository.UserRepository;
import com.axolotl.jobmatcher.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyResponse create(CompanyRequest request, UUID userId) {

        User recruiter = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));

        if (companyRepository.existsByName(request.getName()) ||
                companyRepository.existsByContactEmail(request.getContactEmail())) {
            throw new AppException("Company already exist", HttpStatus.CONFLICT);
        }

        Company company = Company.builder()
                .name(request.getName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .contactEmail(request.getContactEmail())
                .build();

        companyRepository.save(company);

        recruiter.setCompany(company);

        userRepository.save(recruiter);

        return toResponse(company);
    }

    public List<CompanyResponse> getById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException("Company doesn't exist", HttpStatus.NOT_FOUND));
        return List.of(toResponse(company));
    }

    public List<CompanyResponse> getAll(int limit, int offset) {
        if (limit > 100 || limit < 0) {
            throw new AppException("Limit must be less than 100 and bigger than 0", HttpStatus.BAD_REQUEST);
        }
        if (offset < 0) {
            throw new AppException("Offset must be bigger than 0", HttpStatus.BAD_REQUEST);
        }
        return Utils.getResponsePage(companyRepository.findAll(), offset, limit)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Deprecated
    public List<CompanyResponse> search(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CompanyResponse update(CompanyRequest request, UUID userId) {

        Company company = userRepository.findCompanyByUserId(userId)
                .orElseThrow(() -> new AppException("Not found", HttpStatus.NOT_FOUND));

        if ( !company.getName().equals(request.getName())) {
            throw new AppException("Can not change company name", HttpStatus.CONFLICT);
        }

        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setContactEmail(request.getContactEmail());

        return toResponse(companyRepository.save(company));
    }

    public void delete( UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));

        try {
            companyRepository.deleteById(owner.getCompany().getId());
        }
        catch (Exception e) {
            throw new AppException("Company doesn't exist", HttpStatus.CONFLICT);
        }
        owner.setCompany(null);

        userRepository.save(owner);

    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .contactEmail(company.getContactEmail())
                .website(company.getWebsite())
                .createdAt(company.getCreatedAt())
                .build();
    }
}