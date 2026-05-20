package com.camel.clinic.processor.conversation;

import com.camel.clinic.dto.conversation.CreateConversationDto;
import com.camel.clinic.service.conversation.ConversationServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("conversationCreateProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationCreateProcessor implements Processor {
    private final ConversationServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateConversationDto request = exchange.getIn().getBody(CreateConversationDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}