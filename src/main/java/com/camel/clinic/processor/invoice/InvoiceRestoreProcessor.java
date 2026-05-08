package com.camel.clinic.processor.invoice;

import com.camel.clinic.service.invoice.InvoiceServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("invoiceRestoreProcessor")
@AllArgsConstructor
public class InvoiceRestoreProcessor implements Processor {
    private final InvoiceServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
