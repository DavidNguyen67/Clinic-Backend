package com.camel.clinic.service.review;

import com.camel.clinic.entity.Review;
import com.camel.clinic.repository.ReviewRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewServiceInv extends BaseService<Review, ReviewRepository> {
    public ReviewServiceInv(ReviewRepository repository) {
        super(Review::new, repository);
    }

}