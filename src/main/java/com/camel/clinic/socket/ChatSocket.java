package com.camel.clinic.socket;

import com.camel.clinic.dto.chat.ReadReceiptDto;
import com.camel.clinic.dto.chat.TypingPayloadDto;
import com.camel.clinic.dto.message.CreateMessageDto;
import com.camel.clinic.dto.message.ResponseMessageDto;
import com.camel.clinic.service.message.MessageServiceImp;
import com.camel.clinic.service.presence.PresenceServiceImp;
import com.camel.clinic.util.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@AllArgsConstructor
public class ChatSocket {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServiceImp messageServiceImp;
    private final PresenceServiceImp presenceService;

    @MessageMapping("/chat/conversation/{conversationId}")
    public void sendMessage(
        @DestinationVariable String conversationId,
        @Payload CreateMessageDto payload,
        Principal principal
    ) {
        String senderId = principal.getName();

        ResponseMessageDto saved = (ResponseMessageDto)
            messageServiceImp.create(payload, senderId).getBody();

        if (saved != null) {
            saved.setTempId(payload.getTempId());

            messagingTemplate.convertAndSend(
                "/topic/chat/conversation/" + conversationId,
                saved
            );
        }
    }

    @MessageMapping("/typing/conversation/{conversationId}")
    public void typing(
        @DestinationVariable String conversationId,
        @Payload TypingPayloadDto payload,
        Principal principal
    ) {
        String senderId = principal.getName();
        payload.setUserId(senderId);

        messagingTemplate.convertAndSend(
            "/topic/typing/conversation/" + conversationId,
            payload
        );
    }


    @MessageMapping("/read/{messageId}")
    public void markRead(
        @DestinationVariable String messageId,
        Principal principal
    ) {
        messageServiceImp.markAsRead(messageId, principal.getName());
        messagingTemplate.convertAndSend(
            "/topic/message/" + messageId + "/read",
            new ReadReceiptDto(messageId, principal.getName())
        );
    }

    @MessageMapping("/presence/heartbeat")
    public void heartbeat(Principal principal) {
        presenceService.heartbeat(principal.getName());
    }

    @MessageMapping("/presence/online")
    public void goOnline(Principal principal) {
        String userId = principal.getName();
        presenceService.setOnline(userId);

        messagingTemplate.convertAndSend(
            "/topic/presence",
            Map.of("userId", userId, "status", "online")
        );
    }

    @MessageMapping("/presence/offline")
    public void goOffline(Principal principal) {
        String userId = principal.getName();
        presenceService.setOffline(userId);

        messagingTemplate.convertAndSend(
            "/topic/presence",
            Map.of("userId", userId, "status", "offline")
        );
    }


    @MessageMapping("/chat/message/{messageId}/recall")
    public void recallMessage(
        @DestinationVariable String messageId,
        Principal principal
    ) {
        String userId = principal.getName();
        String conversationId = messageServiceImp.recallMessage(messageId, userId);

        if (conversationId != null) {
            messagingTemplate.convertAndSend(
                "/topic/chat/conversation/" + conversationId,
                Map.of(
                    "type", MessageStatus.RECALLED,
                    "messageId", messageId,
                    "recalledBy", userId
                )
            );
        }
    }
}