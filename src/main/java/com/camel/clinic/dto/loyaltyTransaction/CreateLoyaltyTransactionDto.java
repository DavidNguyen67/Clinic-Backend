package com.camel.clinic.dto.loyaltyTransaction;

import com.camel.clinic.entity.LoyaltyTransaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CreateLoyaltyTransactionDto {

    @NotBlank(message = "Patient ID is required")
    private String patientProfileId;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Points is required")
    private Integer points;

    @Size(max = 50, message = "Reference type must not exceed 50 characters")
    private String referenceType;

    private UUID referenceId;

    private String description;

    @NotNull(message = "Balance after is required")
    @Min(value = 0, message = "Balance after must be >= 0")
    private Integer balanceAfter;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date expiresAt;
}