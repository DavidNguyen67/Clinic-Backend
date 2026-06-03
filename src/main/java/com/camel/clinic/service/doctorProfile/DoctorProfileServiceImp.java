package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.dto.doctorProfile.ResponseDoctorProfileDetailDto;
import com.camel.clinic.dto.doctorProfile.UpdateDoctorProfileDto;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.DoctorScheduleException;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.entity.User;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.DoctorScheduleExceptionRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceInv;
import com.camel.clinic.service.specialty.SpecialtyServiceInv;
import com.camel.clinic.service.user.UserServiceInv;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class DoctorProfileServiceImp implements DoctorProfileService {
    private final DoctorProfileServiceInv serviceInv;
    private final DoctorScheduleExceptionServiceInv doctorScheduleExceptionServiceInv;
    private final UserServiceInv userServiceInv;
    private final SpecialtyServiceInv specialtyServiceInv;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorScheduleExceptionRepository doctorScheduleExceptionRepository;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        UUID uuid = CommonService.parseToUuid(id);
        DoctorProfile record = doctorProfileRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("DoctorProfile with ID " + id + " not found"));
        Specification<DoctorScheduleException> spec = Specification.<DoctorScheduleException>unrestricted()
            .and(doctorScheduleExceptionServiceInv.multiFieldEquals(record.getId(), new String[]{"doctorProfile", "id"}))
            .and(doctorScheduleExceptionServiceInv.multiFieldOnDate(CommonService.getCurrentDate(), new String[]{"exceptionDate"}));

        boolean availableToday = doctorScheduleExceptionRepository.findOne(spec).isEmpty();
        ResponseDoctorProfileDetailDto dto = new ResponseDoctorProfileDetailDto(record, availableToday);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> create(CreateDoctorProfileDto requestBody) {
        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setDoctorCode(CommonService.generateDoctorCode());
        doctorProfile.setDegree(requestBody.getDegree());
        doctorProfile.setExperienceYears(requestBody.getExperienceYears());
        doctorProfile.setEducation(requestBody.getEducation());
        doctorProfile.setBio(requestBody.getBio());
        doctorProfile.setConsultationFee(requestBody.getConsultationFee());
        doctorProfile.setIsFeatured(requestBody.getIsFeatured());

        String userId = requestBody.getUserId();
        User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        SecuritiesUtils.requireRole(user, "DOCTOR");
        doctorProfile.setUser(user);

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            doctorProfile.setSpecialty(specialty);
        }

        return serviceInv.create(doctorProfile);
    }

    @Override
    public ResponseEntity<?> bulkCreate(List<CreateDoctorProfileDto> requestBody) {
        List<DoctorProfile> doctorProfiles = requestBody.stream().map(dto -> {
            DoctorProfile doctorProfile = new DoctorProfile();
            doctorProfile.setDoctorCode(CommonService.generateDoctorCode());
            doctorProfile.setDegree(dto.getDegree());
            doctorProfile.setExperienceYears(dto.getExperienceYears());
            doctorProfile.setEducation(dto.getEducation());
            doctorProfile.setBio(dto.getBio());
            doctorProfile.setConsultationFee(dto.getConsultationFee());
            doctorProfile.setIsFeatured(dto.getIsFeatured());

            String userId = dto.getUserId();
            User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found");
            }
            SecuritiesUtils.requireRole(user, "DOCTOR");
            doctorProfile.setUser(user);

            String specialtyId = dto.getSpecialtyId();
            if (specialtyId != null && !specialtyId.isEmpty()) {
                Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
                if (specialty == null) {
                    throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
                }
                doctorProfile.setSpecialty(specialty);
            }

            return doctorProfile;
        }).toList();

        return serviceInv.bulkCreate(doctorProfiles);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateDoctorProfileDto requestBody) {
        DoctorProfile doctorProfile = serviceInv.retrieve(id, null).getBody() instanceof DoctorProfile dp ? dp : null;
        if (doctorProfile == null) {
            throw new IllegalArgumentException("DoctorProfile with ID " + id + " not found");
        }

        doctorProfile.setDegree(requestBody.getDegree());
        doctorProfile.setExperienceYears(requestBody.getExperienceYears());
        doctorProfile.setEducation(requestBody.getEducation());
        doctorProfile.setBio(requestBody.getBio());
        doctorProfile.setConsultationFee(requestBody.getConsultationFee());
        doctorProfile.setIsFeatured(requestBody.getIsFeatured());
        doctorProfile.setTotalPatients(requestBody.getTotalPatients());

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            doctorProfile.setSpecialty(specialty);
        }

        return serviceInv.update(id, doctorProfile, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }
}
