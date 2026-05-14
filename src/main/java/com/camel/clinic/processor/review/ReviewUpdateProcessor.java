package com.camel.clinic.processor.review;

import com.camel.clinic.dto.review.UpdateReviewDto;
import com.camel.clinic.service.review.ReviewServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("reviewUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class ReviewUpdateProcessor implements Processor {
    private final ReviewServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateReviewDto request = exchange.getIn().getBody(UpdateReviewDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}