package com.camel.clinic.dto.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;

@Builder
public record ResponseDoctorProfileDetailDto(@JsonUnwrapped DoctorProfile profile, boolean availableToday) {
}
