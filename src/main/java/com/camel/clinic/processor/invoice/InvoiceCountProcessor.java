package com.camel.clinic.processor.invoice;

import com.camel.clinic.service.invoice.InvoiceServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("invoiceCountProcessor")
@AllArgsConstructor
public class InvoiceCountProcessor implements Processor {
    private final InvoiceServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
