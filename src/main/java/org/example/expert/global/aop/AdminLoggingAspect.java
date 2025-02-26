package org.example.expert.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AdminLoggingAspect {

    private final ObjectMapper objectMapper;

    public AdminLoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    public Object logAdminRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        Object[] args = joinPoint.getArgs();
        AuthUser authUser = (AuthUser) args[0]; // 첫 번째 인자는 로그인한 사용자 정보

        // 요청 데이터 로깅
        String requestBody = objectMapper.writeValueAsString(args);
        log.info("[ADMIN REQUEST] UserID: {}, Method: {}, RequestBody: {}, Time: {}",
                authUser.getId(), methodName, requestBody, LocalDateTime.now());

        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        // 응답 데이터 로깅
        String responseBody = objectMapper.writeValueAsString(result);
        log.info("[ADMIN RESPONSE] UserID: {}, Method: {}, ResponseBody: {}, Time: {}",
                authUser.getId(), methodName, responseBody, LocalDateTime.now());

        return result;
    }
}