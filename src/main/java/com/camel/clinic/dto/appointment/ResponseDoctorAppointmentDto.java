package com.camel.clinic.dto.appointment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDoctorAppointmentDto {
    private long value;
    private long lastMonth;
    private long delta;
    private double deltaPercent;
}
