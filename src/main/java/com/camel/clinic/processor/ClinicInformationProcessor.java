package com.camel.clinic.processor;

import com.camel.clinic.dto.ResponseClinicInformation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component("clinicInformationProcessor")
public class ClinicInformationProcessor implements Processor {

    private final ObjectMapper objectMapper;

    public ClinicInformationProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ClassPathResource resource = new ClassPathResource("clinic-information.json");
        InputStream in = resource.getInputStream();
        ResponseClinicInformation settings = objectMapper.readValue(in, new TypeReference<>() {
        });
        exchange.getIn().setBody(settings);
    }
}

