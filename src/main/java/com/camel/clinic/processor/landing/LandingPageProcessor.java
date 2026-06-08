package com.camel.clinic.processor.landing;

import com.camel.clinic.service.landing.LandingServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("landingPageProcessor")
@AllArgsConstructor
public class LandingPageProcessor implements Processor {
    private final LandingServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = serviceImp.calculateStatistics(queryParams);

        exchange.getMessage().setBody(response);
    }
}
