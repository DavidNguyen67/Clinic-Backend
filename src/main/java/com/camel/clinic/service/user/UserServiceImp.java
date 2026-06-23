package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.dto.user.UpdateUserDto;
import com.camel.clinic.dto.user.UserStatisticsDto;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.auth.AuthServiceInv;
import com.camel.clinic.service.redis.EmailUniqueService;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserServiceInv serviceInv;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUniqueService emailUniqueService;
    private final AuthServiceInv authServiceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> bulkCreate(List<CreateUserDto> requestBody) {
        List<User> users = requestBody.stream().map(dto -> {
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setFullName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setDateOfBirth(dto.getDateOfBirth());
            user.setRole(dto.getRole());
            user.setGender(dto.getGender());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setPathAvatar(dto.getPathAvatar());
            user.setPhoneVerified(false);
            user.setEmailVerified(false);
            return user;
        }).toList();

        return serviceInv.bulkCreate(users);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> create(CreateUserDto req) {
        try {
            String email = req.getEmail().trim().toLowerCase();
            Role.RoleName role = Optional.ofNullable(req.getRole()).orElse(Role.RoleName.PATIENT);

            if (emailUniqueService.existsInCache(email)) {
                throw new BadRequestException("Email already exists");
            }

            if (authServiceInv.findByEmail(email).isPresent()) {
                emailUniqueService.addToCache(email);
                throw new BadRequestException("Email already exists");
            }


            if (authServiceInv.findByPhone(req.getPhone()).isPresent()) {
                throw new BadRequestException("Phone number already exists");
            }

            User user = new User();
            user.setEmail(email);
            user.setFullName(req.getName());
            user.setPhone(req.getPhone());
            user.setDateOfBirth(req.getDateOfBirth());
            user.setRole(role);
            user.setGender(req.getGender());
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            user.setPathAvatar(req.getPathAvatar());
            user.setPhoneVerified(false);
            user.setEmailVerified(false);
            ResponseEntity responseEntity = serviceInv.create(user);

            emailUniqueService.addToCache(email);

            return responseEntity;
        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof ConstraintViolationException cve) {
                cve.getConstraintViolations().forEach(v ->
                        log.error("Validation fail: {} = '{}' → {}",
                                v.getPropertyPath(), v.getInvalidValue(), v.getMessage())
                );
            }
            throw e;
        }
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateUserDto req) {
        try {
            UUID userId = CommonService.parseToUuid(id);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("User not found"));

            String newPhone = req.getPhone() != null
                    ? req.getPhone().trim()
                    : null;

            String newEmail = req.getEmail() != null
                    ? req.getEmail().trim().toLowerCase()
                    : null;

            if (!Objects.equals(newPhone, user.getPhone())) {
                authServiceInv.findByPhone(newPhone).ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new BadRequestException("Phone number already exists");
                    }
                });

                user.setPhone(newPhone);
            }

            if (!Objects.equals(newEmail, user.getEmail())) {
                authServiceInv.findByEmail(newEmail).ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new BadRequestException("Email already exists");
                    }
                });

                // Chỉ cập nhật cache khi email thay đổi
                if (user.getEmail() != null) {
                    emailUniqueService.removeFromCache(user.getEmail());
                }

                emailUniqueService.addToCache(newEmail);
                user.setEmail(newEmail);
            }

            user.setFullName(req.getName());
            user.setDateOfBirth(req.getDateOfBirth());
            user.setGender(req.getGender());
            user.setPathAvatar(req.getPathAvatar());
            user.setRole(req.getRole());

            return serviceInv.update(id, user, null);

        } catch (TransactionSystemException e) {
            Throwable cause = e.getRootCause();

            if (cause instanceof ConstraintViolationException cve) {
                cve.getConstraintViolations().forEach(v ->
                        log.error(
                                "Validation fail: {} = '{}' → {}",
                                v.getPropertyPath(),
                                v.getInvalidValue(),
                                v.getMessage()
                        )
                );
            }

            throw e;
        }
    }

    @Override
    public ResponseEntity<?> calculateStatistics() {
        long patientsCount = serviceInv.countByRole(Role.RoleName.PATIENT, Map.of());
        long doctorsCount = serviceInv.countByRole(Role.RoleName.DOCTOR, Map.of());
        long adminsCount = serviceInv.countByRole(Role.RoleName.ADMIN, Map.of());
        long staffsCount = serviceInv.countByRole(Role.RoleName.STAFF, Map.of());
        UserStatisticsDto statistics = UserStatisticsDto.builder()
                .patientsCount(patientsCount)
                .doctorsCount(doctorsCount)
                .adminsCount(adminsCount)
                .staffsCount(staffsCount)
                .build();
        statistics.calculateTotalCount();

        return ResponseEntity.ok(statistics);
    }

    @Override
    public ResponseEntity<?> countWithSpec(Map<String, Object> queryParams) {
        return serviceInv.countWithSpec(queryParams);
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

}
