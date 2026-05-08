package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment.AppointmentStatus;
import com.camel.clinic.entity.Appointment.BookingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAppointmentDto {
    private String doctorProfileId;

    private String invoiceId;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Future(message = "Appointment date must be in the future")
    private Date appointmentDate;

    private AppointmentStatus status;

    private BookingType bookingType;

    private String reason;

    private String symptoms;

    private String notes;

    private Integer queueNumber;

    @AssertTrue(message = "Invoice ID must be provided when appointment status is CONFIRMED or CHECKED_IN")
    public boolean isInvoiceIdValid() {
        if (status == AppointmentStatus.CONFIRMED || status == AppointmentStatus.CHECKED_IN) {
            return invoiceId != null && !invoiceId.isBlank();
        }
        return true;
    }
}