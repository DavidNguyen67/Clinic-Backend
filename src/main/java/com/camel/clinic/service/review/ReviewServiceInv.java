package com.camel.clinic.service.review;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.review.ReviewDto;
import com.camel.clinic.entity.Review;
import com.camel.clinic.repository.ReviewRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReviewServiceInv extends BaseService<Review, ReviewRepository> {
    public ReviewServiceInv(ReviewRepository repository) {
        super(Review::new, repository);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        ResponseEntity<?> response = super.list(queryParams);
        ApiPaged<Review> resultPage = (ApiPaged<Review>) response.getBody();
        assert resultPage != null;
        List<ReviewDto> reviewDtos = resultPage.getData().stream().map(ReviewDto::from).toList();

        ApiPaged<ReviewDto> paged = new ApiPaged<>(
            reviewDtos,
            resultPage.getTotal(),
            resultPage.getTotalPages(),
            resultPage.getPage(),
            resultPage.getSize()
        );

        return ResponseEntity.ok(paged);
    }

    @Override
    protected Specification<Review> buildSpec(Map<String, Object> queryParams) {
        String doctorProfileId = (String) queryParams.get("doctorProfileId");
        String reviewId = (String) queryParams.get("reviewId");
        String appointmentId = (String) queryParams.get("appointmentId");

        return Specification.<Review>unrestricted()
            .and(notDeleted())
            .and((root, query, cb) -> {
                assert query != null;
                if (!query.getResultType().equals(Long.class)) {
                    root.fetch("patientProfile", JoinType.LEFT);
                    root.fetch("doctorProfile", JoinType.LEFT);
                    query.distinct(true);
                }
                return cb.conjunction();
            })
            .and(multiFieldEquals(CommonService.parseToUuid(reviewId),
                new String[]{"id"}
            ))
            .and(multiFieldEquals(CommonService.parseToUuid(doctorProfileId),
                new String[]{"doctorProfile", "id"}
            ))
            .and(multiFieldEquals(CommonService.parseToUuid(appointmentId),
                new String[]{"appointment", "id"}
            ));
    }

    public ResponseEntity<?> retrieveByAppointmentId(String appointmentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentId", appointmentId);

        Review review = repository.findOne(buildSpec(params))
            .orElseThrow(() -> new RuntimeException("Review not found with appointmentId: " + appointmentId));

        return ResponseEntity.ok(ReviewDto.from(review));
    }

    @Override
    public ResponseEntity<?> retrieve(String id, String fields) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", id);

        Review review = repository.findOne(buildSpec(params))
            .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        return ResponseEntity.ok(ReviewDto.from(review));
    }
}