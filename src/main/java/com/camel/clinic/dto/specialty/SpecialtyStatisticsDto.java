package com.camel.clinic.dto.specialty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SpecialtyStatisticsDto {
    public long totalSpecialties;
}
