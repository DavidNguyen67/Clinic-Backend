package com.camel.clinic.processor.conversation;

import com.camel.clinic.dto.conversation.UpdateConversationDto;
import com.camel.clinic.service.conversation.ConversationServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("conversationUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationUpdateProcessor implements Processor {
    private final ConversationServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateConversationDto request = exchange.getIn().getBody(UpdateConversationDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}