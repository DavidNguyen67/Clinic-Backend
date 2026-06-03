package com.camel.clinic.dto.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record ResponseDoctorProfileDto(@JsonUnwrapped DoctorProfile profile, int serviceCount) {
    public ResponseDoctorProfileDto(DoctorProfile profile, int serviceCount) {
        this.profile = profile;
        this.serviceCount = serviceCount;
    }

    public static ResponseDoctorProfileDto of(DoctorProfile profile) {
        int count = profile.getSpecialty() != null
            && profile.getSpecialty().getServices() != null
            ? profile.getSpecialty().getServices().size()
            : 0;
        return new ResponseDoctorProfileDto(profile, count);
    }
}
