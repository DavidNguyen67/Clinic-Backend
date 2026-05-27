package com.camel.clinic.processor.conversation;

import com.camel.clinic.dto.conversation.CreateConversationDto;
import com.camel.clinic.service.conversation.ConversationServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("conversationCreateProcessor")
@AllArgsConstructor
@Slf4j
public class ConversationCreateProcessor implements Processor {
    private final ConversationServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateConversationDto request = exchange.getIn().getBody(CreateConversationDto.class);

        // Get Authentication directly, then extract the username
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderId = authentication.getName(); // Authentication has getName()

        List<String> participants = Stream.concat(
                        Stream.of(senderId),
                        request.getParticipants().stream()
                )
                .distinct()
                .collect(Collectors.toList());

        request.setParticipants(participants);

        exchange.getIn().setBody(serviceImp.create(request));
    }
}