package com.camel.clinic.service.landing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LandingServiceImp implements LandingService {
    private final LandingServiceInv serviceInv;

    @Override
    public ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams) {
        return serviceInv.calculateStatistics(queryParams);
    }
}
