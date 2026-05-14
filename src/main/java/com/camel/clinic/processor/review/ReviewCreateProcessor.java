package com.camel.clinic.processor.review;

import com.camel.clinic.dto.review.CreateReviewDto;
import com.camel.clinic.service.review.ReviewServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("reviewCreateProcessor")
@AllArgsConstructor
@Slf4j
public class ReviewCreateProcessor implements Processor {
    private final ReviewServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateReviewDto request = exchange.getIn().getBody(CreateReviewDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}