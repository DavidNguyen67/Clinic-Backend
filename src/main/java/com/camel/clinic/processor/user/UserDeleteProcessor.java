package com.camel.clinic.processor.user;

import com.camel.clinic.service.user.UserServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("userDeleteProcessor")
@AllArgsConstructor
public class UserDeleteProcessor implements Processor {
    private final UserServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.delete(id);
        exchange.getIn().setBody(response);
    }
}
