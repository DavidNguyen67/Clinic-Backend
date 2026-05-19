package com.camel.clinic.processor.invoice;

import com.camel.clinic.service.invoice.InvoiceServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("invoiceGetProcessor")
@AllArgsConstructor
@Slf4j
public class InvoiceGetProcessor implements Processor {
    private final InvoiceServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        String appointmentId = exchange.getIn().getHeader("appointmentId", String.class);
        ResponseEntity<?> response;
        if (appointmentId != null) {
            response = serviceImp.retrieveByAppointmentId(appointmentId);
        } else {
            response = serviceImp.retrieve(id);
        }

        exchange.getIn().setBody(response);
    }
}