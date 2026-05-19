package com.camel.clinic.service.invoice;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.entity.Invoice;
import com.camel.clinic.repository.InvoiceRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InvoiceServiceInv extends BaseService<Invoice, InvoiceRepository> {

    public InvoiceServiceInv(InvoiceRepository repository) {
        super(Invoice::new, repository);
    }

    @Override
    protected Specification<Invoice> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Invoice>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("patientProfile", JoinType.LEFT);
                        root.fetch("items", JoinType.LEFT);

                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(multiFieldLike((String) queryParams.get("fullName"),
                                new String[]{"patientProfile", "user", "fullName"}
                        )
                                .and(multiFieldEquals(queryParams.get("patientProfileId"),
                                        new String[]{"patientProfile", "id"}
                                ))
                                .and(multiFieldOnDate(CommonService.parseToDate((String) queryParams.get("invoiceDate")),
                                        new String[]{"invoiceDate"},
                                        new String[]{"createdAt"}
                                ))
                                .and(multiFieldEquals(queryParams.get("appointmentId"),
                                        new String[]{"appointment", "id"}
                                ))
                                .and(multiFieldLike((String) queryParams.get("keyword"),
                                        new String[]{"patientProfile", "user", "fullName"},
                                        new String[]{"invoiceCode"}
                                ))
                                .and(multiFieldBetweenDates(
                                        CommonService.parseToDate((String) queryParams.get("fromDate"), "HH:mm dd/MM/yyyy"),
                                        CommonService.parseToDate((String) queryParams.get("toDate"), "HH:mm dd/MM/yyyy"),
                                        new String[]{"invoiceDate"})
                                )
                                .and(multiFieldIn(parseEnumList(queryParams.get("status"), Invoice.InvoiceStatus.class)
                                        , new String[]{"status"}))
                );
    }

    public boolean isExistInvoiceByAppointmentId(String appointmentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentId", CommonService.parseToUuid(appointmentId));

        long count = repository.count(buildSpec(params));

        return count > 0;
    }

    public ResponseEntity<?> retrieveByAppointmentId(String appointmentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("appointmentId", CommonService.parseToUuid(appointmentId));

        Invoice invoice = repository.findOne(buildSpec(params)).orElse(null);

        return ResponseEntity.ok(invoice);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        int page = parseIntParam(queryParams, "page", 0);
        int size = parseIntParam(queryParams, "size", 20);
        String sortBy = (String) queryParams.getOrDefault("sortBy", "createdAt");
        String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        org.springframework.data.domain.Page<Invoice> resultPage =
                repository.findAll(buildSpec(queryParams), pageable);

        ApiPaged<Invoice> paged = ApiPaged.of(
                resultPage.getContent(),
                resultPage.getTotalElements(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalPages()
        );

        List<Invoice> invoices = resultPage.getContent();

        invoices.forEach(Invoice::calculateTotals);

        return ResponseEntity.ok(paged);
    }
}