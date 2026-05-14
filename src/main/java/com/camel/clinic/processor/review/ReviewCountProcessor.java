package com.camel.clinic.processor.review;

import com.camel.clinic.service.review.ReviewServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("reviewCountProcessor")
@AllArgsConstructor
public class ReviewCountProcessor implements Processor {
    private final ReviewServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
