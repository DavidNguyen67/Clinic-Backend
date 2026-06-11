package com.camel.clinic.service.review;

import com.camel.clinic.dto.review.CreateReviewDto;
import com.camel.clinic.dto.review.UpdateReviewDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Review;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.repository.ReviewRepository;
import com.camel.clinic.service.CommonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ReviewServiceImp implements ReviewService {
    private final ReviewServiceInv serviceInv;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> retrieveByAppointmentId(String appointmentId) {
        return serviceInv.retrieveByAppointmentId(appointmentId);
    }

    @Override
    public ResponseEntity<?> create(CreateReviewDto requestBody) {
        Review review = new Review();
        PatientProfile patientProfile = patientProfileRepository.findById(CommonService.parseToUuid(requestBody.getPatientProfileId()))
            .orElseThrow(() -> new RuntimeException("Patient profile not found with ID: " + requestBody.getPatientProfileId()));

        DoctorProfile doctorProfile = doctorProfileRepository.findById(CommonService.parseToUuid(requestBody.getDoctorProfileId()))
            .orElseThrow(() -> new RuntimeException("Doctor profile not found with ID: " + requestBody.getDoctorProfileId()));

        Appointment appointment = appointmentRepository.findById(CommonService.parseToUuid(requestBody.getAppointmentId()))
            .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + requestBody.getAppointmentId()));

        review.setAppointment(appointment);
        review.setDoctorProfile(doctorProfile);
        review.setPatientProfile(patientProfile);
        review.setRating(requestBody.getRating());
        review.setTitle(requestBody.getTitle());
        review.setContent(requestBody.getContent());

        ResponseEntity<?> response = serviceInv.create(review);

        doctorProfile.setTotalReviews(doctorProfile.getTotalReviews() + 1);

        BigDecimal totalRating = doctorProfile.getAverageRating()
            .multiply(BigDecimal.valueOf(doctorProfile.getTotalReviews() - 1))
            .add(BigDecimal.valueOf(review.getRating()));

        doctorProfile.setAverageRating(totalRating.divide(
            BigDecimal.valueOf(doctorProfile.getTotalReviews()),
            2,
            RoundingMode.HALF_UP
        ));

        doctorProfileRepository.save(doctorProfile);

        return response;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateReviewDto requestBody) {
        Review review = reviewRepository.findById(CommonService.parseToUuid(id))
            .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        review.setRating(requestBody.getRating());
        review.setTitle(requestBody.getTitle());
        review.setContent(requestBody.getContent());

        reviewRepository.save(review);
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }
}
