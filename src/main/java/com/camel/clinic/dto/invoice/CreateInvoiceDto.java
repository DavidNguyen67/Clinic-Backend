// CreateInvoiceDto.java
package com.camel.clinic.dto.invoice;

import com.camel.clinic.dto.invoiceItem.CreateInvoiceItemDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateInvoiceDto {
    private String appointmentId;

    private List<CreateInvoiceItemDto> items = List.of();
}