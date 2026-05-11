package com.camel.clinic.dto.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

@Getter
public class ResponseDoctorProfileDto {

    @JsonUnwrapped
    private final DoctorProfile profile;

    private final int serviceCount;

    public ResponseDoctorProfileDto(DoctorProfile profile) {
        this.profile = profile;
        this.serviceCount = 0;
    }

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
