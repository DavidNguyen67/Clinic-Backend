package com.camel.clinic.dto.loyaltyTransaction;

import com.camel.clinic.entity.LoyaltyTransaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UpdateLoyaltyTransactionDto {

    private TransactionType transactionType;

    private Integer points;

    private String referenceType;

    private UUID referenceId;

    private String description;

    private Integer balanceAfter;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date expiresAt;
}