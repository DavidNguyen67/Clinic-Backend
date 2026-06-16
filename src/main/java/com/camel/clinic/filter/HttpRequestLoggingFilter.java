package com.camel.clinic.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        long startedAt = System.currentTimeMillis();

        response.setHeader(REQUEST_ID_HEADER, requestId);
        log.info("HTTP request started requestId={} method={} path={} query={} remoteAddr={}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                sanitizeQuery(request.getQueryString()),
                request.getRemoteAddr());

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            int status = response.getStatus();

            if (status >= 500) {
                log.error("HTTP request completed requestId={} method={} path={} status={} durationMs={}",
                        requestId, request.getMethod(), request.getRequestURI(), status, durationMs);
            } else if (status >= 400) {
                log.warn("HTTP request completed requestId={} method={} path={} status={} durationMs={}",
                        requestId, request.getMethod(), request.getRequestURI(), status, durationMs);
            } else {
                log.info("HTTP request completed requestId={} method={} path={} status={} durationMs={}",
                        requestId, request.getMethod(), request.getRequestURI(), status, durationMs);
            }
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return requestId;
    }

    private String sanitizeQuery(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return "";
        }
        return queryString.replaceAll("(?i)(token|password|secret|otp|authorization)=([^&]*)", "$1=***");
    }
}
