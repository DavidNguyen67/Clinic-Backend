package com.camel.clinic.processor.message;

import com.camel.clinic.dto.message.UpdateMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("messageUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class MessageUpdateProcessor implements Processor {
    private final MessageServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateMessageDto request = exchange.getIn().getBody(UpdateMessageDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}