package com.camel.clinic.processor.invoice;

import com.camel.clinic.dto.invoice.CreateInvoiceDto;
import com.camel.clinic.service.invoice.InvoiceServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("invoiceCreateProcessor")
@AllArgsConstructor
@Slf4j
public class InvoiceCreateProcessor implements Processor {
    private final InvoiceServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateInvoiceDto request = exchange.getIn().getBody(CreateInvoiceDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}