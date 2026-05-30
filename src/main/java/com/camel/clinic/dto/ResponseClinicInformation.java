package com.camel.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClinicInformation {

    private ClinicDto clinic;
    private ContactDto contact;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicDto {
        private String name;
        private String description;
        private String about;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactDto {
        private String address;
        private String hotline;
        private String email;
        private WorkingHoursDto workingHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHoursDto {
        private String mondayToSaturday;
        private String sunday;
    }
}