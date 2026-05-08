package com.camel.clinic.service.medicalRecord;

import com.camel.clinic.entity.MedicalRecord;
import com.camel.clinic.repository.MedicalRecordRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MedicalRecordServiceInv extends BaseService<MedicalRecord, MedicalRecordRepository> {
    public MedicalRecordServiceInv(MedicalRecordRepository repository) {
        super(MedicalRecord::new, repository);
    }

    @Override
    protected Specification<MedicalRecord> buildSpec(Map<String, Object> queryParams) {
        return Specification.<MedicalRecord>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("appointment", JoinType.LEFT);
                    }
                    return cb.conjunction();
                })
                .and(multiFieldEquals(CommonService.parseToUuid(queryParams.get("patientProfileId")),
                        new String[]{"patientProfile", "id"}
                ))
                .and(multiFieldOnDate(CommonService.parseToDate((String) queryParams.get("appointmentDate")),
                        new String[]{"appointment", "appointmentDate"},
                        new String[]{"createdAt"}
                ));
    }
}