package com.axolotl.jobmatcher.service;

// service/UserService.java

import com.axolotl.jobmatcher.dto.RegisterRequest;
import com.axolotl.jobmatcher.dto.user.LoginRequest;
import com.axolotl.jobmatcher.dto.user.LoginResponse;
import com.axolotl.jobmatcher.dto.user.UserResponse;
import com.axolotl.jobmatcher.entity.User;
import com.axolotl.jobmatcher.exception.AppException;
import com.axolotl.jobmatcher.repository.UserRepository;
import com.axolotl.jobmatcher.security.JwtService;
import com.axolotl.jobmatcher.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email is already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .company(null)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));
        return toResponse(user);
    }

    @Deprecated
    public List<UserResponse> getAll(int offset, int limit) {

        Utils.validatePaging(offset, limit);

        return userRepository.findAll(PageRequest.of(offset / limit, limit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Email doesn't exist", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    @Deprecated
    public void removeCompany(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User doesn't exist", HttpStatus.NOT_FOUND));
        user.setCompany(null);
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .companyName(user.getCompany() != null ? user.getCompany().getName() : null)
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
