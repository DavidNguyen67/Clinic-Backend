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

    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .status(review.getStatus())
                .build();
    }
}