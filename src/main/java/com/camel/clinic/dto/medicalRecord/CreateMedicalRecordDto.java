package com.camel.clinic.dto.medicalRecord;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateMedicalRecordDto {
    @NotNull(message = "Appointment ID is required")
    private String appointmentId;

    private String chiefComplaint;

    private Map<String, Object> vitalSigns;

    private String diagnosis;

    private String treatmentPlan;
}