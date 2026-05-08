package com.camel.clinic.service.loyaltyTransaction;

import com.camel.clinic.entity.LoyaltyTransaction;
import com.camel.clinic.repository.LoyaltyTransactionRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LoyaltyTransactionServiceInv extends BaseService<LoyaltyTransaction, LoyaltyTransactionRepository> {
    public LoyaltyTransactionServiceInv(LoyaltyTransactionRepository repository) {
        super(LoyaltyTransaction::new, repository);
    }

    @Override
    protected Specification<LoyaltyTransaction> buildSpec(Map<String, Object> queryParams) {
        return Specification.<LoyaltyTransaction>unrestricted()
                .and(notDeleted())
                .and(multiFieldLike((String) queryParams.get("fullName"),
                                new String[]{"patientProfile", "user", "fullName"}
                        )
                                .and(multiFieldEquals(queryParams.get("patientProfileId"),
                                        new String[]{"patientProfile", "id"}
                                ))
                                .and(multiFieldIn(
                                        parseEnumList(queryParams.get("transactionType"), LoyaltyTransaction.TransactionType.class),
                                        new String[]{"transactionType"}
                                ))
                );
    }
}