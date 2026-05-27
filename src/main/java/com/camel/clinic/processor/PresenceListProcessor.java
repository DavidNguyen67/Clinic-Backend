package com.camel.clinic.processor;

import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("presenceListProcessor")
@AllArgsConstructor
public class PresenceListProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
    }
}
