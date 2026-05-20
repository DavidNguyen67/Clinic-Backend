package com.camel.clinic.dto.review;

import com.camel.clinic.entity.Review;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReviewDto {
    UUID id;
    Integer rating;
    String title;
    String content;
    Review.ReviewStatus status;
    String patientProfileId;
    String patientName;
    String patientPathAvatar;
    String doctorProfileId;
    String doctorName;

    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .status(review.getStatus())
                .patientPathAvatar(review.getPatientProfile().getUser().getPathAvatar() != null
                        ? review.getPatientProfile().getUser().getPathAvatar()
                        : null)
                .patientProfileId(review.getPatientProfile() != null
                        ? review.getPatientProfile().getId().toString()
                        : null)
                .patientName(review.getPatientProfile() != null
                        ? review.getPatientProfile().getUser().getFullName()
                        : null)
                .doctorProfileId(review.getDoctorProfile() != null
                        ? review.getDoctorProfile().getId().toString()
                        : null)
                .doctorName(review.getDoctorProfile() != null
                        ? review.getDoctorProfile().getUser().getFullName()
                        : null)
                .build();
    }
}