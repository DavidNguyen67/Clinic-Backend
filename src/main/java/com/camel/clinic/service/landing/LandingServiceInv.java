package com.camel.clinic.service.landing;

import com.camel.clinic.dto.landing.ResponseLandingDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.Review;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.ReviewRepository;
import com.camel.clinic.service.BaseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LandingServiceInv {
    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final BaseService baseService;
    private final EntityManager entityManager;

    public LandingServiceInv(AppointmentRepository appointmentRepository,
                             ReviewRepository reviewRepository,
                             DoctorProfileRepository doctorProfileRepository,
                             @Qualifier("userServiceInv") BaseService baseService,
                             EntityManager entityManager) {
        this.appointmentRepository = appointmentRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.baseService = baseService;
        this.entityManager = entityManager;
    }


    public ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams) {
        Specification reviewSpec = buildReviewSpec();

        long yearsOfExperience = 15;

        ResponseLandingDto response = ResponseLandingDto.builder()
            .trustedPatients(countCompletedAppointments())
            .experience(yearsOfExperience)
            .specialistDoctors(countSpecialistDoctors())
            .satisfaction(calculateSatisfaction(getAverageRating(reviewSpec)))
            .build();

        return ResponseEntity.ok(response);
    }


    private long countCompletedAppointments() {
        Specification spec = Specification.<Appointment>unrestricted()
            .and(baseService.notDeleted())
            .and(baseService.multiFieldIn(
                List.of(Appointment.AppointmentStatus.COMPLETED),
                new String[]{"status"}
            ));
        return appointmentRepository.count(spec);
    }

    private long countSpecialistDoctors() {
        Specification spec = Specification.<DoctorProfile>unrestricted()
            .and(baseService.notDeleted())
            .and((root, query, cb) -> cb.isNotNull(root.get("specialty")));
        return doctorProfileRepository.count(spec);
    }

    private Specification buildReviewSpec() {
        return Specification.<Review>unrestricted()
            .and(baseService.notDeleted());
    }

    private Double calculateSatisfaction(Double averageRating) {
        return averageRating <= 0 ? 0.0 : 98.0;
    }

    public Double getAverageRating(Specification<Review> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> query = cb.createQuery(Double.class);
        Root<Review> root = query.from(Review.class);

        Predicate predicate = specification.toPredicate(root, query, cb);

        query.select(cb.coalesce(cb.avg(root.get("rating")), 0.0));

        if (predicate != null) {
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();
    }
}