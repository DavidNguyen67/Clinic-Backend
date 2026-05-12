package com.camel.clinic.dto.mailSender;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailPayload {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
    private String htmlBody;
}