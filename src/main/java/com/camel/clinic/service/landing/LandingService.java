package com.camel.clinic.service.landing;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface LandingService {
    ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams);
}
