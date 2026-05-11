package com.camel.clinic.processor.appointment;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Role;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("appointmentCalculateStatisticsProcessor")
@AllArgsConstructor
public class AppointmentCalculateStatisticsProcessor implements Processor {
    private final AppointmentServiceImp serviceImp;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        Role.RoleName role = SecuritiesUtils.getRole();

        String userId = jwtUtil.getUserIdFromToken(SecuritiesUtils.getAccessToken(exchange));
        if (role == Role.RoleName.PATIENT) {
            PatientProfile profile = patientProfileRepository.findByUserId(CommonService.parseToUuid(userId))
                    .orElseThrow(() -> new RuntimeException(
                            "Patient profile not found for user ID: " + userId));

            queryParams.put("patientProfileId", profile.getId().toString());

        }
        if (role == Role.RoleName.DOCTOR) {
            doctorProfileRepository.findByUserId(CommonService.parseToUuid(userId))
                    .orElseThrow(() -> new RuntimeException(
                            "Doctor profile not found for user ID: " + userId));

            queryParams.put("doctorProfileId", userId);
        }

        ResponseEntity<?> response = serviceImp.calculateStatistics(queryParams);

        exchange.getMessage().setBody(response);
    }
}
