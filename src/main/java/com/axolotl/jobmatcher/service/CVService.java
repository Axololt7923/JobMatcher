package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.cv.CVResponse;
import com.axolotl.jobmatcher.entity.CV;
import com.axolotl.jobmatcher.entity.CVParsedData;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.CVParsedDataRepository;
import com.axolotl.jobmatcher.repository.CVRepository;
import com.axolotl.jobmatcher.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CVRepository cvRepository;
    private final CVParsedDataRepository cvParsedDataRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    @Transactional
    public CVResponse upload(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));

        cvRepository.findByUserIdAndIsActiveTrue(userId)
                .ifPresent(oldCV -> {
                    oldCV.setIsActive(false);
                    aiService.deletedJobVector(oldCV.getChromaId());
                    cvRepository.save(oldCV);
                });

        AIService.CVParsedResult parsed = aiService.parseCV(file.getBytes(), user.getId());

//      save the .pdf
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/cv");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        CV cv = CV.builder()
                .user(user)
                .fileUrl(filePath.toString())
                .isActive(true)
                .build();

        cv.setChromaId(parsed.getChromaId());
        cv = cvRepository.save(cv);

        CVParsedData parsedData = CVParsedData.builder()
                .cv(cv)
                .skills(parsed.getSkills())
                .experienceYears(parsed.getExperienceYears())
                .educationLevel(parsed.getEducationLevel())
                .languages(parsed.getLanguages())
                .summary(parsed.getSummary())
                .rawJson(new ObjectMapper().writeValueAsString(parsed))
                .build();
        cvParsedDataRepository.save(parsedData);
        return toResponse(cv, parsedData);
    }

    public List<CVResponse> getByUserId(UUID userId) {
        return cvRepository.findByUserId(userId)
                .stream()
                .map(cv -> {
                    CVParsedData parsed = cvParsedDataRepository
                            .findByCvId(cv.getId()).orElse(null);
                    return toResponse(cv, parsed);
                })
                .toList();
    }

    public void delete(UUID id, UUID userId) {
        CV cv = cvRepository.findById(id)
                .orElse(null);
        if (cv == null || !cv.getUser().getId().equals(userId)) {
            throw new AppException("No permission", HttpStatus.FORBIDDEN);
        }
        try {
            aiService.deleteCVParsedVectors(cv.getChromaId());
            cvRepository.deleteById(id);

            Path path = Paths.get(cv.getFileUrl());

            boolean result = Files.deleteIfExists(path);

            if (result) {
                System.out.println("Deleted file: " + path);
            } else {
                System.out.println("File not found");
            }
        } catch (IOException e) {
            System.err.println("Error catch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException("Failed to delete CV", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private CVResponse toResponse(CV cv, CVParsedData parsed) {
        CVResponse.CVResponseBuilder builder = CVResponse.builder()
                .id(cv.getId())
                .userId(cv.getUser().getId())
                .fileUrl(cv.getFileUrl())
                .chromaId(cv.getChromaId())
                .isActive(cv.getIsActive())
                .createdAt(cv.getCreatedAt());

        if (parsed != null) {
            builder.skills(parsed.getSkills())
                    .experienceYears(parsed.getExperienceYears())
                    .educationLevel(parsed.getEducationLevel())
                    .languages(parsed.getLanguages())
                    .summary(parsed.getSummary());
        }

        return builder.build();
    }
}