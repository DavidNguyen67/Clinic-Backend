package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.dto.user.UpdateUserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> bulkCreate(List<CreateUserDto> requestBody);

    ResponseEntity<?> count();

    ResponseEntity<?> create(CreateUserDto request);

    ResponseEntity<?> update(String id, UpdateUserDto request);

    ResponseEntity<?> calculateStatistics();

    ResponseEntity<?> countWithSpec(Map<String, Object> queryParams);

    ResponseEntity<?> retrieve(String id);
}
