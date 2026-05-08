package com.camel.clinic.processor.loyaltyTransaction;

import com.camel.clinic.service.loyaltyTransaction.LoyaltyTransactionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("loyaltyTransactionDeleteProcessor")
@AllArgsConstructor
@Slf4j
public class LoyaltyTransactionDeleteProcessor implements Processor {
    private final LoyaltyTransactionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.delete(id);
        exchange.getIn().setBody(response);
    }
}