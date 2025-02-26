package org.example.expert.global.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
        String requestBody = convertToJson(args);

        log.info("[ADMIN REQUEST] Method: {}, RequestBody: {}, Time: {}",
                methodName, requestBody, LocalDateTime.now());

        Object result = joinPoint.proceed();

        String responseBody = convertToJson(result);

        log.info("[ADMIN RESPONSE] Method: {}, ResponseBody: {}, Time: {}",
                methodName, responseBody, LocalDateTime.now());

        return result;
    }
    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "Error converting to JSON";
        }
    }
}