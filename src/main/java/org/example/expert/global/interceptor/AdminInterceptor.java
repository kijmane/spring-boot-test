package org.example.expert.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.example.expert.global.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = userService.getAuthenticatedUserId(); // 현재 로그인한 사용자 ID 조회
        if (userId == null) {
            throw new UnauthorizedAccessException("사용자 인증 실패");
        }

        UserRole userRole = userService.getUserRoleById(userId); // 사용자 역할 조회
        if (userRole != UserRole.ADMIN) {
            throw new UnauthorizedAccessException("어드민 권한이 필요합니다.");
        }

        log.info("[Admin Request] UserID: {}, URL: {}, Time: {}",
                userId, request.getRequestURI(), LocalDateTime.now());

        return true;
    }
}