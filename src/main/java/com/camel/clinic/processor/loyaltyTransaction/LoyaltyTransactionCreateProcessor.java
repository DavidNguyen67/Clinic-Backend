package com.camel.clinic.processor.loyaltyTransaction;

import com.camel.clinic.dto.loyaltyTransaction.CreateLoyaltyTransactionDto;
import com.camel.clinic.service.loyaltyTransaction.LoyaltyTransactionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("loyaltyTransactionCreateProcessor")
@AllArgsConstructor
@Slf4j
public class LoyaltyTransactionCreateProcessor implements Processor {
    private final LoyaltyTransactionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateLoyaltyTransactionDto request = exchange.getIn().getBody(CreateLoyaltyTransactionDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}