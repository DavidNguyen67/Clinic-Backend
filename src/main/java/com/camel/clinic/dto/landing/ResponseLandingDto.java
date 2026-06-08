package com.camel.clinic.dto.landing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseLandingDto {
    private Long trustedPatients;
    private Long experience;
    private Long specialistDoctors;
    private Double satisfaction;
}
