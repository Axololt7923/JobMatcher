package com.axolotl.jobmatcher.service;

import com.axolotl.jobmatcher.dto.cv.CVResponse;
import com.axolotl.jobmatcher.entity.CV;
import com.axolotl.jobmatcher.entity.CVParsedData;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.CVParsedDataRepository;
import com.axolotl.jobmatcher.repository.CVRepository;
import com.axolotl.jobmatcher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public CVResponse upload(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));

        String fileName  = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath  = Paths.get("uploads/cv");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        cvRepository.findByUserIdAndIsActiveTrue(userId)
                .ifPresent(oldCV -> {
                    oldCV.setIsActive(false);
                    cvRepository.save(oldCV);
                });

        CV cv = CV.builder()
                .user(user)
                .fileUrl(filePath.toString())
                .isActive(true)
                .build();
        cv = cvRepository.save(cv);

        AIService.CVParsedResult parsed = aiService.parseCV(file.getBytes(), user.getId());

        cv.setChromaId(parsed.getChromaId());
        cv = cvRepository.save(cv);

        CVParsedData parsedData = CVParsedData.builder()
                .cv(cv)
                .skills(parsed.getSkills())
                .experienceYears(parsed.getExperienceYears())
                .educationLevel(parsed.getEducationLevel())
                .languages(parsed.getLanguages())
                .summary(parsed.getSummary())
                .rawJson(parsed.getRawJson())
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

    public CVResponse getById(UUID id) {
        CV cv = cvRepository.findById(id)
                .orElseThrow(() -> new AppException("CV doesn't exist", HttpStatus.NOT_FOUND));
        CVParsedData parsed = cvParsedDataRepository
                .findByCvId(id).orElse(null);
        return toResponse(cv, parsed);
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