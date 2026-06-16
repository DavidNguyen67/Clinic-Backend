package com.camel.clinic.processor.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userCreateProcessor")
@AllArgsConstructor
@Slf4j
public class UserCreateProcessor implements Processor {
    private final UserServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateUserDto request = exchange.getIn().getBody(CreateUserDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}