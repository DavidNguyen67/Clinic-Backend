package com.camel.clinic.processor.conversation;

import com.camel.clinic.service.conversation.ConversationServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("conversationListProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationListProcessor implements Processor {
    private final ConversationServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderId = authentication.getName(); // Authentication has getName()
        queryParams.put("userId", senderId);

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}