package com.camel.clinic.processor.htmlTemplate;

import com.camel.clinic.dto.mailSender.EmailPayload;
import com.camel.clinic.service.CommonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component("htmlTemplateProcessor")
@AllArgsConstructor
@Slf4j
public class HtmlTemplateProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws IOException {
        EmailPayload payload = CommonService.parsePayload(exchange.getIn().getBody(), EmailPayload.class);
        assert payload != null;
        String template = payload.getTemplateName();
        Map<String, Object> vars = payload.getVariables();

        ClassPathResource resource = new ClassPathResource("static/templates/" + template + ".html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        payload.setHtmlBody(html);

        exchange.getIn().setBody(payload);
    }
}