package com.camel.clinic.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ServiceImpLoggingAspect {

    @Around("execution(public * com.camel.clinic.service..*ServiceImp.*(..))")
    public Object logServiceImpMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = method.getName();
        String operation = className + "." + methodName;
        long startedAt = System.currentTimeMillis();

        if (log.isInfoEnabled()) {
            log.info("Service operation started operation={} args={}",
                    operation,
                    summarizeArguments(signature.getParameterNames(), joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            long durationMs = System.currentTimeMillis() - startedAt;
            log.info("Service operation completed operation={} durationMs={} result={}",
                    operation,
                    durationMs,
                    summarizeResult(result));
            return result;
        } catch (Throwable ex) {
            long durationMs = System.currentTimeMillis() - startedAt;
            log.error("Service operation failed operation={} durationMs={} errorType={} message={}",
                    operation,
                    durationMs,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    private String summarizeArguments(String[] parameterNames, Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return java.util.stream.IntStream.range(0, args.length)
                .mapToObj(i -> {
                    String name = parameterNames != null && i < parameterNames.length
                            ? parameterNames[i]
                            : "arg" + i;
                    return name + "=" + summarizeValue(name, args[i]);
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String summarizeValue(String name, Object value) {
        if (value == null) {
            return "null";
        }

        String lowerName = name == null ? "" : name.toLowerCase();
        if (lowerName.contains("password")
                || lowerName.contains("token")
                || lowerName.contains("secret")
                || lowerName.contains("otp")
                || lowerName.contains("authorization")) {
            return "***";
        }

        if (value instanceof UUID || value instanceof Number || value instanceof Boolean || value instanceof Enum<?>) {
            return value.toString();
        }

        if (value instanceof CharSequence text) {
            String raw = text.toString();
            if (looksLikeUuid(raw) || lowerName.contains("id") || lowerName.contains("email")) {
                return sanitizeScalar(raw);
            }
            return value.getClass().getSimpleName() + "(length=" + raw.length() + ")";
        }

        if (value instanceof Date date) {
            return date.toInstant().toString();
        }

        if (value instanceof Collection<?> collection) {
            return value.getClass().getSimpleName() + "(size=" + collection.size() + ")";
        }

        if (value instanceof Map<?, ?> map) {
            return summarizeMap(map);
        }

        return value.getClass().getSimpleName();
    }

    private String summarizeMap(Map<?, ?> map) {
        String keys = map.keySet().stream()
                .limit(10)
                .map(String::valueOf)
                .map(this::sanitizeKey)
                .collect(Collectors.joining(","));
        return "Map(size=" + map.size() + ", keys=[" + keys + "])";
    }

    private String summarizeResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof ResponseEntity<?> response) {
            Object body = response.getBody();
            String bodyType = body == null ? "null" : body.getClass().getSimpleName();
            return "ResponseEntity(status=" + response.getStatusCode().value() + ", bodyType=" + bodyType + ")";
        }
        if (result instanceof Collection<?> collection) {
            return result.getClass().getSimpleName() + "(size=" + collection.size() + ")";
        }
        if (result instanceof Map<?, ?> map) {
            return "Map(size=" + map.size() + ")";
        }
        if (result instanceof CharSequence text) {
            return result.getClass().getSimpleName() + "(length=" + text.length() + ")";
        }
        return result.getClass().getSimpleName();
    }

    private String sanitizeScalar(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replaceAll("(?i)(token|password|secret|otp|authorization)=([^&\\s]*)", "$1=***");
    }

    private String sanitizeKey(String key) {
        String lowerKey = key.toLowerCase();
        if (lowerKey.contains("password")
                || lowerKey.contains("token")
                || lowerKey.contains("secret")
                || lowerKey.contains("otp")
                || lowerKey.contains("authorization")) {
            return key + ":***";
        }
        return key;
    }

    private boolean looksLikeUuid(String value) {
        return value.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
}
