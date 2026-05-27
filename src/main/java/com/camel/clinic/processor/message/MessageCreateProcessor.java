package com.camel.clinic.processor.message;

import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.message.MessageServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("messageCreateProcessor")
@AllArgsConstructor
@Slf4j
public class MessageCreateProcessor implements Processor {
    private final MessageServiceImp serviceImp;
    private final JwtUtil jwtUtil;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;


    @Override
    public void process(Exchange exchange) throws Exception {
        CreateMessageDto request = exchange.getIn().getBody(CreateMessageDto.class);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderId = authentication.getName(); // Authentication has getName()

        ResponseEntity<?> response = serviceImp.create(request, senderId);
        exchange.getIn().setBody(response);
    }
}