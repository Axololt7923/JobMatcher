package com.axolotl.jobmatcher.dto.user;

import com.axolotl.jobmatcher.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
    private UUID companyId;
    private String companyName;
}