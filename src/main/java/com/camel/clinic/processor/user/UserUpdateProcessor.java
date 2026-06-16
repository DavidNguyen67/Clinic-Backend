package com.camel.clinic.processor.user;

import com.camel.clinic.dto.user.UpdateUserDto;
import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class UserUpdateProcessor implements Processor {
    private final UserServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateUserDto request = exchange.getIn().getBody(UpdateUserDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}