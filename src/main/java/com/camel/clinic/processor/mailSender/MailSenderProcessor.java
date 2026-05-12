package com.camel.clinic.processor.mailSender;

import com.camel.clinic.dto.mailSender.EmailPayload;
import com.camel.clinic.service.CommonService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component("mailSenderProcessor")
@Slf4j
@AllArgsConstructor
public class MailSenderProcessor implements Processor {

    private final JavaMailSender mailSender;

    @Override
    public void process(Exchange exchange) throws Exception {
        EmailPayload payload = CommonService.parsePayload(exchange.getIn().getBody(), EmailPayload.class);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        assert payload != null;
        helper.setTo(payload.getTo());
        helper.setSubject(payload.getSubject());
        helper.setText(payload.getHtmlBody(), true);   // true = isHtml
        helper.setFrom("noreply@clinic.com");

        mailSender.send(message);
        log.info("[Mail] Sent to {}", payload.getTo());
    }
}