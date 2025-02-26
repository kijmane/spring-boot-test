package org.example.expert.domain.user.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, @Valid UserChangePasswordRequest userChangePasswordRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        // 기존 비밀번호와 일치하는지 확인
        if(!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        // 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        // 새로운 비밀번호를 암호화하여 저장
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    // 현재 인증된 사용자 ID 가져오기
    @Transactional(readOnly = true)
    public Long getAuthenticatedUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new InvalidRequestException("인증된 사용자를 찾을 수 없습니다."));
    }

    // 사용자 역할 가져오기
    @Transactional(readOnly = true)
    public UserRole getUserRoleById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUserRole)
                .orElseThrow(() -> new InvalidRequestException("User not found"));
    }
}
