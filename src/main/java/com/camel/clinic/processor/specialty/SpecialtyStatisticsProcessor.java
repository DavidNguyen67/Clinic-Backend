package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("specialtyStatisticsProcessor")
@AllArgsConstructor
@Slf4j
public class SpecialtyStatisticsProcessor implements Processor {
    private final SpecialtyServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        ResponseEntity<?> response = serviceImp.calculateStatistics(queryParams);
        exchange.getIn().setBody(response);
    }
}
