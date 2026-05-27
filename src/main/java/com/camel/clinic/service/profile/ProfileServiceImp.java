package com.camel.clinic.service.profile;

import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.user.UserServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final UserServiceInv userServiceInv;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        Specification<User> spec = userServiceInv.buildSpec(queryParams);
        List<User> users = userRepository.findAll(spec);

        return ResponseEntity.ok(users);
    }
}
