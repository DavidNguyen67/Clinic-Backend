package com.camel.clinic.processor.message;

import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("messageCreateProcessor")
@AllArgsConstructor
@Slf4j
public class MessageCreateProcessor implements Processor {
    private final MessageServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateMessageDto request = exchange.getIn().getBody(CreateMessageDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}