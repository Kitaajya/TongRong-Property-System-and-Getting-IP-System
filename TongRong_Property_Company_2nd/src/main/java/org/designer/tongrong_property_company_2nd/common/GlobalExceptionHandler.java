package org.designer.tongrong_property_company_2nd.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足：{}", e.getMessage());
        return Map.of("success", false, "message", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数错误：{}", e.getMessage());
        return Map.of("success", false, "message", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Map.of("success", false, "message", "系统异常，请稍后重试");
    }
}
