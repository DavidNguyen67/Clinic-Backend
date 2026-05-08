package com.camel.clinic.service.loyaltyTransaction;

import com.camel.clinic.dto.loyaltyTransaction.CreateLoyaltyTransactionDto;
import com.camel.clinic.dto.loyaltyTransaction.UpdateLoyaltyTransactionDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LoyaltyTransactionServiceImp implements LoyaltyTransactionService {
    private final LoyaltyTransactionServiceInv serviceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateLoyaltyTransactionDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateLoyaltyTransactionDto requestBody) {
        return null;

    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }
}
